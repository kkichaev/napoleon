use std::{collections::{HashMap, hash_map::Entry}, sync::{Arc, Mutex}, net::SocketAddr, io::Error};
use rand::Rng;
use tokio::{net::{TcpStream, TcpListener}, signal, sync::{watch::{self}, mpsc::{self, Sender}}, io::{AsyncReadExt, AsyncWriteExt}};
use tracing::{debug};

use crate::{connection::Connection, command::{Command, HttpData}, server::{ServerData, self}, gh::{ClientRequest, GlobalHandlers, log_handler, LogEntry}};

const SERVER_EXISTS : &str = "Сервер с таким ID уже запущен";
const WRONG_SERVER_ID : &str = "Не верный ID сервера";
const NO_SERVER_AVAIL : &str = "Сервер не доступен";
const NO_SOCKETS : &str = "Нет свободных подключений";
const VERSION: &str = "1.0.0.1";

const MIN_PORT : u32 = 10000;
const MAX_PORT : u32 = 11000;

fn format_id(id:u32) -> String {
    return format!("{:}.{:}.{:}.{:}", 
        (id & 0xff000000) >> 24, 
        (id & 0xff0000) >> 16, 
        (id & 0xff00) >> 8,
        (id & 0xff));
}

fn check_server(servers:&Arc<Mutex<HashMap<u32, ServerData>>>, server:&ServerData, server_id:u32) -> bool{
    let srvs = &mut servers.lock().unwrap();
    if let Entry::Occupied(cur_server) = srvs.entry(server_id) {
        if !cur_server.get().same_address(&server.addr) {
            return false;
        }
        srvs.remove(&server_id);
        debug!("Server reconnecting {}", format_id(server_id));
    }
    return true;
}

async fn exchanging(mut server: Connection, mut client: Connection, handlers:GlobalHandlers, http_mode:bool) {

    let mut buf1 = vec![0; 4096];
    let mut buf2 = vec![0; 4096];

    let mut ccb = 0;
    let mut scb = 0;

    let _ = handlers.log_tx.send(LogEntry::ClientStarting(client.id, server.id)).await;

    loop {
        tokio::select! {
            cr = client.stream.read(&mut buf1[..]) => {
                if cr.is_err() {
                    break;
                }
                let mut cr = cr.unwrap();
                if cr == 0 {
                    break;
                }
                if http_mode {
                    if let Ok(hs) = String::from_utf8(buf1[..4].to_vec()) {
                        if hs.to_lowercase() == "get " {
                            let buf = buf1[5..].to_vec();
                            let pos = buf.windows(1).position(|w| w == b"/").unwrap();
                            let data_len = buf.len() - pos;
    
                            buf1[..4].copy_from_slice(b"GET ");
                            buf1[4..data_len+4].copy_from_slice(&buf[pos..]);
                            cr = data_len + 4;
                        }    
                    }
                }
                ccb += cr;
                let _ = server.stream.write_all(&buf1[..cr]).await;
            }
            cr = server.stream.read(&mut buf2[..]) => {
                if cr.is_err() {
                    break;
                }
                let cr = cr.unwrap();
                if cr == 0 {
                    break;
                }
                scb += cr;
                let _ = client.stream.write_all(&buf2[..cr]).await;
            }
        }
    }

    let cmd = Command::Close(client.id);
    let _ = cmd.write(&mut server.stream).await;

    let _ = handlers.log_tx.send(LogEntry::ClientClosed(client.id, server.id, ccb as u32, scb as u32)).await;
    debug!("Close client");
    
}

async fn handle_exchange(mut client: Connection, server_id:u32, listener : TcpListener, handlers:GlobalHandlers) {
    
    let res = listener.accept().await;
    if let Ok((stream, _)) = res {
        let mut server = Connection::new(stream);
        server.id = server_id;

        if let Ok(Some(data)) = server.read().await {
            match data {
                Command::OK(id) => {
                    let cmd = Command::OK(id);
                    let _ = cmd.write(&mut client.stream).await;
                    let handlers = handlers.clone();

                    client.id = id;
                    exchanging(server, client, handlers, false).await;
                }
                _ => {
                }
            }
        }        
    } else {
        debug!("Accept error {:?}", res);
    }

    // let _ = handlers.req_port_tx.send(FreePortRequest::FreePort(port)).await;
}

async fn handle_http_exchange(mut client: Connection, server_id:u32, listener : TcpListener, handlers:GlobalHandlers, http_data:HttpData) {

    let res = listener.accept().await;
    if let Ok((stream, _)) = res {
        let mut server = Connection::new(stream);
        server.id = server_id;

        if let Ok(Some(data)) = server.read().await {
            match data {
                Command::OK(id) => {
                    let handlers = handlers.clone();

                    let mut header = "GET ".to_string() + &http_data.url + "\r\n";
                    for (k, v) in &http_data.headers {
                        header = header + k + ":" + v + "\r\n";
                    }
                    header = header + "\r\n";

                    let _ = server.stream.write(header.as_bytes()).await;
                    client.id = id;
                    exchanging(server, client, handlers, true).await;
                }
                _ => {
                }
            }
        }        
    } else {
        debug!("Accept error {:?}", res);
    }

    // let _ = handlers.req_port_tx.send(FreePortRequest::FreePort(port)).await;
}

async fn handle_server(servers:Arc<Mutex<HashMap<u32, ServerData>>>, server_id:u32, mut connection: Connection, addr:SocketAddr, handlers:GlobalHandlers) {
    let (tx, mut rx) = mpsc::channel(20);
    let server_data = ServerData::new(tx, addr);

    if !check_server(&servers, &server_data, server_id) {
        let cmd = Command::Reject(SERVER_EXISTS.to_string());
        let _ = cmd.write(&mut connection.stream).await;

        debug!("Server with id {} from other addr {}", format_id(server_id), addr);
        return;
    }


    debug!("New server {} ({server_id}) at {}", format_id(server_id), server_data.addr);
    {
        let mut servers = servers.lock().unwrap();
        servers.insert(server_id, server_data);
    }
    let _ = handlers.log_tx.send(LogEntry::ServerStarting(server_id, addr)).await;

    let cmd = Command::OK(server_id);
    let _ = cmd.write(&mut connection.stream).await;
    let mut stop_rx = handlers.stop_rx.clone();

    tokio::select! {
        _ = async {
            loop {
                tokio::select! {
                    res = connection.read() => {
                        if let Ok(data) = res {
                            if let Some(data) = data {
                                // debug!("Got command {:?}", data);
                                server::handle(data, &mut connection).await;
                            } else {
                                debug!("Server closed {:?}", addr);
                                let _ = handlers.log_tx.send(LogEntry::ServerClosed(server_id, addr)).await;
                                break
                            }    
                        } else {
                            debug!("Connection error {:?}", res);
                            break
                        }
                    }

                    Some(cli_req) = rx.recv() => {
                        let handlers = handlers.clone();
                        match cli_req {
                            ClientRequest::Client(con, port) => {
                                debug!("Accepting client on {port}");
                                let addr = format!("0.0.0.0:{}", port);
                                let res = TcpListener::bind(addr).await;
                                if let Ok(listener) = res {
                                    let cmd = Command::ClienConnect(port);
                                    let _ = cmd.write(&mut connection.stream).await;
                                    handle_exchange(con, server_id, listener, handlers).await;
                                } else {
                                    debug!("Binding error {:?}", res);
                                }
                            }
                            ClientRequest::HttpClient(con, port, http_data) => {
                                debug!("Accepting client on {port}");
                                let addr = format!("0.0.0.0:{}", port);
                                let res = TcpListener::bind(addr).await;
                                if let Ok(listener) = res {
                                    let cmd = Command::ClienConnect(port);
                                    let _ = cmd.write(&mut connection.stream).await;
                                    handle_http_exchange(con, server_id, listener, handlers, http_data).await;
                                } else {
                                    debug!("Binding error {:?}", res);
                                }
                            }                            
                        }
                    }
                }
            }
        } => {}
        _ = stop_rx.changed() => {
            debug!("Get stop");
        }
    }
    
    {
        debug!("Remove server {server_id} from servers");
        let mut servers = servers.lock().unwrap();
        servers.remove(&server_id);
    }
}

async fn find_free_port(min_port:u32, max_port:u32) -> Option<u32> {
    let cp = rand::thread_rng().gen_range(min_port..=max_port);
    
    for i in cp..=max_port {
        if let Ok(_) = TcpListener::bind(("0.0.0.0", i as u16)).await {
            return Some(i as u32);
        }
    }

    for i in min_port..cp {
        if let Ok(_) = TcpListener::bind(("0.0.0.0", i as u16)).await {
            return Some(i as u32);
        }
    }
    return None;
}

async fn handle_client(servers:Arc<Mutex<HashMap<u32, ServerData>>>, server_id:u32, mut connection: Connection, 
    addr:SocketAddr) {

    debug!("Handle client {:?} to server {server_id}", addr);

    let mut tx : Option<Sender<ClientRequest>> = None;
    {
        let servers = servers.lock().unwrap();
        if let Some(data) = servers.get(&server_id) {
            tx = Some(data.sender.clone());
        }
    }

    if tx.is_none() {
        debug!("No server {}", format_id(server_id));

        let cmd = Command::Reject(NO_SERVER_AVAIL.to_string());
        let _ = cmd.write(&mut connection.stream).await;
        return;    
    }

    if let Some(port) = find_free_port(MIN_PORT, MAX_PORT).await {
        let res = tx.unwrap().send(ClientRequest::Client(connection, port)).await;            
        if res.is_err() {
            debug!("Error while req client connection server closed");
            // let _ = handlers.req_port_tx.send(FreePortRequest::FreePort(port)).await;
        }
    } else {
        debug!("No free ports");

        let cmd = Command::Reject(NO_SOCKETS.to_string());
        let _ = cmd.write(&mut connection.stream).await;
        return; 
    }

}

async fn send_html(stream: &mut TcpStream, message: &str) {
    let status_line = "HTTP/1.1 200 OK";
    let content = format!("<html><head></head><body>{message}</body></html>");
    let length = content.len();

    let response =
        format!("{status_line}\r\nContent-Length: {length}\r\nContent-type: text/html; charset=UTF-8\r\n\r\n{content}");

    let _ = stream.write(response.as_bytes()).await;
}

async fn handle_http_client(servers:Arc<Mutex<HashMap<u32, ServerData>>>, mut connection: Connection, 
    addr:SocketAddr, http_data : HttpData) {

    debug!("Handle http client {:?} to server {}", addr, http_data.server_id);

    let mut tx : Option<Sender<ClientRequest>> = None;
    {
        let servers = servers.lock().unwrap();
        if let Some(data) = servers.get(&http_data.server_id) {
            tx = Some(data.sender.clone());
        }
    }

    if tx.is_none() {
        debug!("No server {}", format_id(http_data.server_id));

        send_html(&mut connection.stream, NO_SERVER_AVAIL).await;
        return;    
    }

    if let Some(port) = find_free_port(MIN_PORT, MAX_PORT).await {
        let res = tx.unwrap().send(ClientRequest::HttpClient(connection, port, http_data)).await;            
        if res.is_err() {
            debug!("Error while req client connection server closed");
            // let _ = handlers.req_port_tx.send(FreePortRequest::FreePort(port)).await;
        }
    } else {
        debug!("No free ports");

        let cmd = Command::Reject(NO_SOCKETS.to_string());
        let _ = cmd.write(&mut connection.stream).await;
        return; 
    }
}

async fn handle_connection(servers:Arc<Mutex<HashMap<u32, ServerData>>>, socket:TcpStream, addr:SocketAddr, handlers:GlobalHandlers ) {
    let mut connection = Connection::new(socket);
    
    // let addr_str = addr.to_string();
    // let span = span!(Level::DEBUG, "connection", addr_str);
    // let _enter = span.enter();

    let command = 
        match connection.read().await {
            Ok(data) => match data {
                Some(command) => {
                    command
                }
                None => {
                    return;
                }
            }
            Err(e) => {
                debug!("Error while connecting {:?}", e);
                return;
            }
        };
    
    match command {
        Command::ServerConnect(server_id) => {
            if !server::is_server_id(server_id) {
                debug!("Received id {} is not a server id", format_id(server_id));

                let cmd = Command::Reject(WRONG_SERVER_ID.to_string());
                let _ = cmd.write(&mut connection.stream).await;
                return;
            }
            handle_server(servers, server_id, connection, addr, handlers).await;
        }
        Command::ClienConnect(server_id) => {
            handle_client(servers, server_id, connection, addr).await;
        }
        Command::HttpClientConnect(http_data) => {
            handle_http_client(servers, connection, addr, http_data).await;
        }
        _ => {
            debug!("Wrong first command {:?}", command);
            return;
        }
    }
}

pub async fn start(addr : String) -> Result<(), Error> {
    let servers : Arc<Mutex<HashMap<u32, ServerData>>>  = Arc::new(Mutex::new(HashMap::new()));

    let (stop_tx, stop_rx) = watch::channel(0);
    // let (req_port_tx, port_rx) = mpsc::channel(100);
    let (log_tx, log_rx) = mpsc::channel(100);

    let (close_tx, close_rx) = tokio::sync::oneshot::channel();
    let gh = GlobalHandlers::new(stop_rx, log_tx, close_tx);

    debug!("Starting listen ver {}", VERSION);

    let listener = TcpListener::bind(addr).await?;
    // let h_threads = Arc::new(Mutex::new(Vec::new()));

    let lh = tokio::spawn(log_handler(log_rx, gh.clone()));

    let srvs = servers.clone();
    let g_handler = gh.clone();
    // let h_handlers = h_threads.clone();
    tokio::select! {
        _ = tokio::spawn( async move {
            loop {
                if let Ok((socket, addr)) = listener.accept().await {
                    let servers1 = srvs.clone();
                    let handlers = g_handler.clone();
                    let _ = tokio::spawn(async move {
                        handle_connection(servers1, socket, addr, handlers).await;
                    });
                    // {
                    //     let mut hh = h_handlers.lock().unwrap();
                    //     hh.push(handle);
                    // }
                }        
            }
        }) => {}
        _ = signal::ctrl_c() => {}
        _ = close_rx =>{}
    }

    stop_tx.send(1).unwrap();
    // free resource and shutdown

    {
        // let mut hh = h_threads.lock().unwrap();
        // for h in hh.iter_mut() {
        //     h.await?;
        // }    
    }
    // fh.await?;
    lh.await?;
    
    Ok(())
}