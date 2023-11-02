use std::io::Cursor;

use tokio::io::AsyncReadExt;
use tokio::{net::TcpStream};
use bytes::{BytesMut, Buf};

use crate::command::Command;
use crate::errors::Error;

pub struct Connection {
    pub stream : TcpStream,
    pub id: u32,
    buffer : BytesMut,
}

impl Connection {
    pub fn new(stream: TcpStream) -> Connection {
        Connection {
            stream,
            buffer: BytesMut::with_capacity(5 * 1024),
            id: 0,
        }
    }

    pub async fn read(&mut self) -> Result<Option<Command>, Error> {
        loop {
            if let Some(command) = self.parse_command()? {
                return Ok(Some(command));
            }
    
            match self.stream.read_buf(&mut self.buffer).await {
                Ok(val) => {
                    if val == 0 {
                        // client closed
                        return Ok(None);
                    }
                }
                Err(e) => {
                    return Err(Error::IOError(e));
                }
                
            }
        }
    }

    fn parse_command(&mut self) -> Result<Option<Command>, Error> {
        let mut buf = Cursor::new(&self.buffer[..]);

        match Command::parse( &mut buf) {
            Ok(command) => {
                let len = buf.position() as usize;
                self.buffer.advance(len);

                Ok(Some(command))
            }
            Err(crate::command::Error::Incomplete) => {
                Ok(None)
            }
            _ => {
                Err(Error::UndefCommand)
            }
        }
    }
}