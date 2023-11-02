use std::{fs, os::unix::prelude::PermissionsExt, sync::Arc};
use serde_json::Value;
use tokio::{net::{UnixListener, UnixStream}, io::{BufReader, AsyncBufReadExt}, sync::{mpsc::{Sender}, Mutex, oneshot}};
use tracing::debug;

use crate::{Config, error::{HandlerError, HandlerResult, Commands}, CommandType};

async fn do_command(cmd:CommandType, cmd_tx: &Sender<CommandType>, mtx:Arc<Mutex<i32>>) -> Result<(), HandlerError> {
    let _g = mtx.lock();
    let res = cmd_tx.send(cmd).await;
    if res.is_err() {
        let res = res.err().unwrap();
        let msg = format!("{:?}", res);
        return Err(HandlerError::ErrorMessage(msg));
    }
    return Ok(());
}

fn err_response(cmd:&str) -> HandlerError {
    let msg = format!("Bad command <{}>", cmd);
    debug!(msg);
    HandlerError::ErrorMessage(msg)
}

pub async fn handle_command(cmd:&str, cmd_tx: &Sender<CommandType>, mtx:Arc<Mutex<i32>>) -> HandlerResult {
    let mut c = cmd.trim();
    let mut params: Option<Value> = None;

    if let Some(idx) = c.find(" ") {
        let res : serde_json::Result<Value> = serde_json::from_str(&c[idx+1..].trim());
        if res.is_ok() {
            params = Some(res.unwrap());
        }
        c = &c[0..idx];
    }

    let (atx, arx) = oneshot::channel();

    if let Some(cmd) = Commands::from(c, params) {
        let cmd = (atx, cmd);
        let _ = do_command(cmd, cmd_tx, mtx).await?;
    } else {
        return Err(err_response(cmd));
    }

    let res = arx.await?;
    if res.is_err() {
        let msg = format!("{:?}", res.err().unwrap());
        return Err(HandlerError::ErrorMessage(msg));
    }
    return Ok(res.unwrap());
}

pub async fn handle(mut stream: UnixStream, cmd_tx:Sender<CommandType>, mtx:Arc<Mutex<i32>>) {
    let (rx, tx) = stream.split();
    let mut reader = BufReader::new(rx);

    let mut cmd = String::new();
    
    while reader.read_line(&mut cmd).await.unwrap() > 0 {
        let res = handle_command(&cmd, &cmd_tx, mtx.clone()).await;
        let msg = if res.is_ok() {
                res.unwrap().to_string()
            } else {
                format!("Error {:?}", res.err())
            };
        
        let _ = tx.try_write(msg.as_bytes());
        cmd.clear();
    }
    debug!("end session");
}

pub fn start_cmd_handler(cfg: &Config, cmd_tx:Sender<CommandType>, command_mutex:Arc<Mutex<i32>>) {
    let val = cfg.cmd_socket.as_str();
    debug!("Starting on {}", val);

    let _ = fs::remove_file(val.clone());
    let listener = UnixListener::bind(val.clone()).unwrap();
    fs::set_permissions(val, fs::Permissions::from_mode(0o666)).unwrap();

    // let answ_rx = Arc::new(Mutex::new(answ_rx));
    tokio::spawn(async move {
        loop {
            if let Ok((stream, _)) = listener.accept().await {
                handle(stream, cmd_tx.clone(), command_mutex.clone()).await;
            }                
        }
    });
}