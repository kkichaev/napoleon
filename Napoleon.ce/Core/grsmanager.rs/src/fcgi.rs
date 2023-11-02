use serde_json::json;
use tokio::io::{AsyncRead, AsyncWrite};
use tokio::net::TcpListener;
use tokio::sync::mpsc::Sender;
use tokio::sync::{oneshot, Mutex};
use tokio::time::sleep;
use tokio_fastcgi::{Request, RequestResult, Requests};
use core::marker::{Unpin, Send};
use std::collections::HashMap;
use std::io::{Read, Write};
use std::net::SocketAddr;
use std::os::unix::net::UnixStream;
use std::sync::Arc;
use std::time::Duration;
use tracing::debug;
use urldecode;

use crate::CommandType;
use crate::cmdhandler::handle_command;
use crate::error::{Commands, HandlerError, HandlerResponse, make_server_answer};

enum ResponseCode {
    OK,
    BadRequest,
    Unauthorized,
    Forbidden,
}

struct Response {
    pub code :i32,
    pub message: String,
}

impl Response {
    fn from(code:ResponseCode) -> Response {
        return match code {
            ResponseCode::OK => Response { code: 200, message: "OK".to_owned() },
            ResponseCode::BadRequest => Response { code: 400, message: "Bad Request".to_owned() },
            ResponseCode::Unauthorized => Response { code: 401, message: "Unauthorized".to_owned() },
            ResponseCode::Forbidden => Response { code: 403, message: "Forbidden".to_owned() },
        }
    }
}

async fn send_answer<W:AsyncWrite + Unpin>(request: Arc<Request<W>>, code:ResponseCode, cnt_type:&str, msg:&str) {
    let resp = Response::from(code);
    let bytes = msg.as_bytes();

    let _ = request.get_stdout().write(
        format!("Status: {} {}\r\nContent-Type: {}\r\nContent-Length: {}\r\n\r\n"
            ,resp.code, resp.message
            ,cnt_type
            , bytes.len()).as_bytes()).await;
    let _ = request.get_stdout().write(msg.as_bytes()).await;
}

async fn is_server_running(server_code:&str, db_tx:&Sender<CommandType>) -> bool {
    let mut ret = true;

    let cmd = Commands::IsServerRuning(String::from(server_code));
    
    let msg = format!("Check is running {}", server_code);
    debug!(msg);

    let res = send_command_to_db(&db_tx, cmd).await;
    if res.is_ok() {
        if let HandlerResponse::ServerRunning(running) = res.unwrap() {
            ret = running;
        }
    }

    return ret;
}

async fn send_command_to_db(db_tx:&Sender<CommandType>, cmd:Commands) -> Result<HandlerResponse, HandlerError> {
    let (atx, arx) = oneshot::channel();
    let cmd = (atx, cmd);
    let res = db_tx.send(cmd).await;
    if res.is_err() {
        let res = res.err().unwrap();
        let msg = format!("{:?}", res);
        return Err(HandlerError::ErrorMessage(msg));
    }

    return arx.await?;
}

async fn get_server_socket(auth:&str, server_code:&str, db_tx:&Sender<CommandType>) -> Result<(String, String), HandlerError> {
    let mut cmd = None;
    if server_code.len() > 0 {
        cmd = Some(Commands::GetServerSocket(String::from(server_code), false));
    } else {
        let aa = auth.split(" ").collect::<Vec<&str>>();
        if let Some(server_token) = aa.get(1) {
            cmd = Some(Commands::GetServerSocket(String::from(*server_token), true));
        }
    }

    if cmd.is_some() {
        let res = send_command_to_db(db_tx, cmd.unwrap()).await;

        // let cmd = cmd.unwrap();
        // let (atx, arx) = oneshot::channel();
        // let cmd = (atx, cmd);
        // let res = db_tx.send(cmd).await;
        // if res.is_err() {
        //     let res = res.err().unwrap();
        //     let msg = format!("{:?}", res);
        //     return Err(HandlerError::ErrorMessage(msg));
        // }
    
        // let res = arx.await?;
        if res.is_err() {
            return Err(res.err().unwrap());
        }
        let res = res.unwrap();
        if let HandlerResponse::ServerSocket(sock, code) = res {
            return Ok((sock, code));
        }
    }
    Err(HandlerError::NoRecords)
}

async fn process_request<W:AsyncWrite + Unpin>(request: Arc<Request<W>>, prefix:&str, db_tx:Sender<CommandType>, mtx:Arc<Mutex<i32>>) -> Result<(), HandlerError> {
    let mut content_length:i32 = 0;
    let mut req_method = String::new();
    let mut http_ver= String::new();
    let mut uri = String::new();
    let mut host = String::new();
    // let mut content_type: String= String::new();
    let mut authorization = String::new();
    let mut server_code = String::new();

    let mut command = String::new();
    let mut no_wakeup = false;

    let mut headers:HashMap<String,String> = HashMap::new();
    let hpref = "http_";

    if let Some(params) = request.str_params_iter() {
        for param in params {
            if  param.1.is_none() {
                continue;
            }
            let value = param.1.unwrap().to_owned();
            
            // debug!("{}: {:?}", param.0, value);

            let mut key = String::from(param.0).to_lowercase();
            if key.starts_with(hpref) {
                key = String::from(&key[hpref.len()..]);
            
                // http headers handle
                match key.as_str() {                    
                    "grcommand" => { command = value },
                    "authorization" => { authorization = value },
                    "host" => { host = value },
                    _ => { headers.insert(key.replace("_", "-"), value); }
                }
                continue;
            }

            match key.as_str() {
                "no_wakeup" => {
                    if value.len() > 0 { 
                        let iv:i32 = value.parse().unwrap();
                        no_wakeup = iv > 0;
                    }
                },
                "server_code" => { server_code = value },
                "content_length" => {
                    if value.len() > 0 { content_length = value.parse().unwrap() }
                },
                "request_method" => { req_method = value },
                "server_protocol" => { http_ver = value },
                "request_uri" => { 
                    if value.starts_with(prefix) {
                        let tstr = &value[prefix.len()..];
                        uri = String::from(tstr);
                    } else {
                        uri = value;
                    }
               },
                _ => { 
                    // debug!("{}: {:?}", param.0, value) 
                }
            }
          }
    }

    if !command.is_empty() {
        let command = urldecode::decode(command);
        let answ = handle_command(&command, &db_tx, mtx).await;
        if answ.is_err() {
            let msg = answ.err().unwrap().to_response();
            debug!("{}",msg);
            send_answer(request, ResponseCode::BadRequest, "application/json", &msg).await;
        } else {
            let msg = answ.unwrap().to_response();
            debug!("{}",msg);
            send_answer(request, ResponseCode::OK, "application/json", &msg).await;
        }
        return Ok(());
    }

    if req_method.len() == 0 || http_ver.len() == 0 || uri.len() == 0 {
        let msg = "No required headers";
        debug!(msg);
        return Err(HandlerError::ErrorMessage(msg.to_owned()));
    }

    // debug!("auth={}, serv_code={}", authorization, server_code);
    if authorization.len() == 0 && server_code.len() == 0 {
        let msg = "Unauthorized";
        debug!(msg);
        send_answer(request, ResponseCode::Unauthorized, "text/plain", msg).await;
        return Ok(());
    }

    if no_wakeup {
        if !is_server_running(&server_code, &db_tx).await {
            let msg = json!([make_server_answer(false, "server not running")]).to_string();
            send_answer(request, ResponseCode::OK, "text/plain", &msg).await;
            return Ok(());
        }
    }

    let (serv_socket, server_code) = get_server_socket(&authorization, &server_code, &db_tx).await?;
    let mut res= UnixStream::connect(&serv_socket);
    for _ in 0..5 {
        if res.is_ok() {
            break;
        }
        sleep(Duration::from_millis(100)).await;
        res = UnixStream::connect(&serv_socket);
    }

    if res.is_err() {
        let msg = format!("Err connect socket {} {:?}", &serv_socket, res.err());
        debug!(msg);
        return Err(HandlerError::ErrorMessage(msg));
    }

    debug!("Connect to server {}", &serv_socket);

    let mut header = format!(
"{} {} {}\r
Host: {}\r
Authorization: {}\r
Content-Type: application/json; charset=utf-8\r
Content-Length: {}\r\n"
    , req_method, uri, http_ver
    , host
    , format!("Bearer {}", server_code)
    , content_length);
    
    for (k,v) in headers {
        header = header + &format!("{}: {}\r\n", k, v);
    }
    header = header + "\r\n";

    let mut stream = res.unwrap();
    let res = stream.write(header.as_bytes());
    if res.is_err() {
        debug!("Error writng header {:?}", res.err());
    }
    // debug!(header);

    // send content to grserver
    while content_length > 0 {
        let mut bf = [0; 2000];

        let mut data = request.get_stdin();
        let n = data.read(&mut bf)?;
        let _ = stream.write(&bf[..n]);
        content_length -= n as i32;
    }

    loop {
        let mut bf = [0; 2000];
        let n = stream.read(&mut bf[..])?;        
        if n == 0 {
            break;
        }
        // let str = String::from_utf8(bf[..n].to_vec()).unwrap();
        // debug!("{}", str);
        let _ = request.get_stdout().write(&bf[..n]).await?;
    } 

    return Ok(());

}

async fn handler<R: AsyncRead + Unpin + Send, W:AsyncWrite + Unpin + Send>(r:R, w:W
    , prefix:&str, db_tx:Sender<CommandType>, ctx : Arc<Mutex<i32>>) {
    let mut requests = Requests::new(r, w, 10, 10);
    
    loop {
        let res = requests.next().await;
        if let Ok(Some(request)) = res {
            let db_tx = db_tx.clone();
            let ctx = ctx.clone();
            if let Err(err) = request.process(|_request| async move {
                let res = process_request(_request.clone(), prefix, db_tx, ctx).await;
                if res.is_err() {
                    let err = res.err().unwrap().to_response();
                    send_answer(_request, ResponseCode::BadRequest, "application/json", &err).await;
                }
                RequestResult::Complete(0)
            }).await {
                // This is the error handler that is called if the process call returns an error.
                debug!("Processing request failed: {}", err);
            }
        } else {
            if let Err(error) = res {
                debug!("Next req  error {:?}", error);
            }
            break;
        }
    }

    // while let Ok(Some(request)) = requests.next().await {
    //     let db_tx = db_tx.clone();
    //     let ctx = ctx.clone();
    //     if let Err(err) = request.process(|_request| async move {
    //         let res = process_request(_request.clone(), prefix, db_tx, ctx).await;
    //         if res.is_err() {
    //             let err = res.err().unwrap().to_response();
    //             send_answer(_request, ResponseCode::BadRequest, "application/json", &err).await;
    //         }
    //         RequestResult::Complete(0)
    //     }).await {
    //         // This is the error handler that is called if the process call returns an error.
    //         debug!("Processing request failed: {}", err);
    //     }
    // }   
}

pub async fn start_tcp(listener:TcpListener, db_tx:Sender<CommandType>, prefix:String, command_mutex:Arc<Mutex<i32>>) {
    loop {
        let connection = listener.accept().await;
        let ctx = command_mutex.clone();
        match connection {
            Err(err) => {
                debug!("Establishing connection failed: {}", err);
				break;
            },
            Ok((mut stream, address)) => {
                debug!("Connection from {}", address);
                let db_tx = db_tx.clone();
                let prefix = String::from(&prefix);
                tokio::spawn(async move {
                    let (r, w) = stream.split();
                    handler(r, w, &prefix, db_tx, ctx).await;
                });
            }
        }
    }
}

pub async fn start_fcgi_handler(addr:String, prefix:String, db_tx:Sender<CommandType>, command_mutex:Arc<Mutex<i32>>) {
    let listener = TcpListener::bind(addr.parse::<SocketAddr>().unwrap()).await.unwrap();
    loop {
        let connection = listener.accept().await;
        let ctx = command_mutex.clone();
        match connection {
            Err(err) => {
                debug!("Establishing connection failed: {}", err);
				break;
            },
            Ok((mut stream, address)) => {
                debug!("Connection from {:?}", address);
                let db_tx = db_tx.clone();
                let prefix = String::from(&prefix);
                tokio::spawn(async move {
                    let (r, w) = stream.split();
                    handler(r, w, &prefix, db_tx, ctx).await;
                });
            }
        }
    }
}