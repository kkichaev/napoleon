

use std::{net::SocketAddr};

use tokio::sync::mpsc::Sender;

use crate::{connection::Connection, command::Command, gh::ClientRequest};

pub struct ServerData {
    pub addr : SocketAddr,
    pub sender : Sender<ClientRequest>,
}

impl ServerData {
    pub fn new(sender : Sender<ClientRequest>, addr : SocketAddr) -> ServerData {
        ServerData { addr, sender }
    }

    pub fn same_address(self: &Self, addr: &SocketAddr) -> bool {
        return addr.ip().eq(&self.addr.ip())
    }
    
}

pub fn is_server_id(id:u32) -> bool {
    return (id & 0x80000000) != 0;
}

pub async fn handle(command: Command, connection : &mut Connection) {
    match command {
        Command::Ping => {
            let _ = command.write(&mut connection.stream).await;
            // debug!("send Ping");
        }
        _ => {}
    }
}

