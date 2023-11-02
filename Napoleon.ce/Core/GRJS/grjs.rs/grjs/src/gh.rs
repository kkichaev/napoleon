use std::{sync::{Arc, Mutex}, net::SocketAddr, time::{UNIX_EPOCH, SystemTime}, mem};

use tokio::sync::mpsc::{Receiver};
use tracing::debug;

use crate::{connection::Connection, command::HttpData};

pub enum ClientRequest {
    Client(Connection, u32),  // connection + port
    HttpClient(Connection, u32, HttpData) // 
}

pub enum FreePortRequest {
    ReqPort(tokio::sync::oneshot::Sender<Option<u32>>),
    FreePort(u32)
}

pub enum LogEntry {
    ServerStarting(u32, SocketAddr), // server id, server addr
    ClientStarting(u32, u32), // client id, server id
    ClientClosed(u32, u32, u32, u32), // client id, server id, client bytes, server bytes
    ServerClosed(u32, SocketAddr)
}

pub struct GlobalHandlers {
    pub stop_rx: tokio::sync::watch::Receiver<i8>,
    pub log_tx: tokio::sync::mpsc::Sender<LogEntry>,

    stop_sender: Arc<Mutex<Option<tokio::sync::oneshot::Sender<u32>>>>,
    errors_count: Arc<Mutex<u32>>
}

impl GlobalHandlers {
    pub fn new(
        stop_rx: tokio::sync::watch::Receiver<i8>,
        log_tx: tokio::sync::mpsc::Sender<LogEntry>,
        stop_sender: tokio::sync::oneshot::Sender<u32>
    ) -> GlobalHandlers {

            GlobalHandlers {stop_rx, log_tx,
                stop_sender:Arc::new(Mutex::new(Some(stop_sender))), 
                errors_count:Arc::new(Mutex::new(0))}
        }

    pub fn add_error(&mut self) {
        debug!("Adding error");

        let mut val = self.errors_count.lock().unwrap();
        *val += 1;

        if *val > 5 {
            debug!("The error threshold level occurred. Stoping execution");
            let mut mtx = self.stop_sender.lock().unwrap();
            if let Some(sender) = mem::replace(&mut *mtx, None) {
                tokio::spawn(async move{
                    let _ = sender.send(1);
                });
            }
        }
    }
}

impl Clone for GlobalHandlers {
    fn clone(&self) -> Self {
        let stop_rx = self.stop_rx.clone();
        let log_tx = self.log_tx.clone();

        GlobalHandlers{stop_rx, log_tx, 
            stop_sender:self.stop_sender.clone(),
            errors_count:self.errors_count.clone()}
    }
}

use serde::{Serialize};
use serde_json::Result;

const ACTION_OPEN:u32 = 0;
const ACTION_CLOSE:u32 = 1;
const LOG_URL:&str = "https://grsoft.ru//grjs/grjs.php";

#[allow(non_snake_case)]
#[derive(Serialize)]
struct ClientLog {
    id:u32,
    cid:u32,
    action:u32,
    date:u64,
    duration:u64,
    traficClient:u32,
    traficServer:u32,
}

#[derive(Serialize)]
struct ServerLog {
    id:u32,
    action:u32,
    date:u64,
    address:String,
}

#[derive(Serialize)]
struct Log {
    clients:Vec<ClientLog>,
    servers:Vec<ServerLog>
}

impl ClientLog {
    fn new(id:u32, server_id:u32) -> ClientLog {
        let time = SystemTime::now().duration_since(UNIX_EPOCH).unwrap();
        ClientLog { id, cid: server_id, action: ACTION_OPEN, date: time.as_secs(), duration: 0, traficClient: 0, traficServer: 0 }
    }

    fn new_close(id:u32, server_id:u32, cli_cb:u32, srv_cb: u32, clients:&Vec<ClientLog>) -> ClientLog {
        let time = SystemTime::now().duration_since(UNIX_EPOCH).unwrap();
        let mut duration = 0;
        for c in clients {
            if c.id == id && c.cid == server_id {
                duration = time.as_secs() - c.date;
                break;
            }
        }
        ClientLog { id, cid: server_id, action: ACTION_CLOSE, date: time.as_secs(), duration, traficClient: cli_cb, traficServer: srv_cb }
    }
}

impl ServerLog {
    fn new(id:u32, addr:SocketAddr) -> ServerLog {
        let time = SystemTime::now().duration_since(UNIX_EPOCH).unwrap();
        ServerLog { id, action: ACTION_OPEN, date: time.as_secs(), address: addr.to_string() }
    }
    fn new_close(id:u32, addr:SocketAddr) -> ServerLog {
        let time = SystemTime::now().duration_since(UNIX_EPOCH).unwrap();
        ServerLog { id, action: ACTION_CLOSE, date: time.as_secs(), address: addr.to_string() }
    }
}

impl Log {
    fn count(self:&Self) -> u32 {
        return (self.clients.len() + self.servers.len()) as u32;
    }

    fn clear(self:&mut Self) {
        self.clients.clear();
        self.servers.clear();
    }

    async fn write(self: &mut Self) -> Result<()> {
        if self.count() > 0 {
            let data = serde_json::to_string(self)?;
            let cli = reqwest::Client::new();
        
            let res = cli.post(LOG_URL).body(data).send().await;
            if res.is_ok()  {
                self.clear();
            } else {
                debug!("POST log error {:?}", res.err().unwrap());
            }
            // debug!("json log {data}");
        }
        Ok(())
    }
}

pub async fn log_handler(mut rx : Receiver<LogEntry>, mut handlers : GlobalHandlers) {
    debug!("Start log handler");

    let mut log = Log{clients:Vec::new(), servers:Vec::new()};

    loop {
        tokio::select! {
            Some(entry) = rx.recv() => {
                match entry {
                    LogEntry::ServerStarting(id, addr) => {
                        log.servers.push(ServerLog::new(id, addr));
                    }
                    LogEntry::ClientStarting(id, server_id) => {
                        log.clients.push(ClientLog::new(id, server_id));
                    }
                    LogEntry::ClientClosed(id, server_id, cli_cb, srv_cb) => {
                        log.clients.push(ClientLog::new_close(id, server_id, cli_cb, srv_cb, &log.clients));
                    }
                    LogEntry::ServerClosed(id, addr) => {
                        log.servers.push(ServerLog::new_close(id, addr));
                    }
                }
                if log.count() > 100 {
                    let _ = log.write().await;
                }
            }
            _ = handlers.stop_rx.changed() => {
                break;
            }
        }
    }
    let _ = log.write().await;
    debug!("Stop log handler");
}