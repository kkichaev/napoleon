use std::collections::HashMap;
use std::io::{Cursor, self};
use std::str;

use bytes::{Buf, BufMut};

use tokio::{net::{TcpStream}, io::AsyncWriteExt};


pub enum Error {
    Undef,
    Incomplete,
    NotAPacket,
    UnexpectedCommand(u32),

    Other(crate::errors::Error),
}

#[derive(Debug)]
pub struct HttpData {
    pub server_id:u32,
    pub url : String,
    pub headers : HashMap<String,String>,
}

#[derive(Debug)]
pub enum Command {
    ServerConnect(u32), // server id
    ClienConnect(u32),  // allocated port
    HttpClientConnect(HttpData), // server, url, headers

    OK(u32), // server id
    Ping,
    Close(u32),

    Reject(String), // reject reason
    Data(Vec<u8>), // data
}

const PACKET_TAG :u32 = u32::from_be_bytes(*b"GRJS");
const SERVER_CONNECT :u32 = u32::from_be_bytes(*b"CNCT");
const CLIENT_CONNECT :u32 = u32::from_be_bytes(*b"CLTC");
const OK_CMD :u32 = u32::from_be_bytes(*b"OKCM");
const REJECT_CMD :u32 = u32::from_be_bytes(*b"RJCT");
const PING_CMD :u32 = u32::from_be_bytes(*b"PING");
const DATA_CMD :u32 = u32::from_be_bytes(*b"DATA");
const CLOSE_CMD :u32 = u32::from_be_bytes(*b"CLOS");

impl HttpData {

    pub fn from(src:&mut Cursor<&[u8]>) -> Result<HttpData, Error> {
        let mut first = true;
        let mut url = "".to_string();
        let mut id:u32 = 0;
        let mut headers:HashMap<String, String> = HashMap::new();

        let crlf = b"\r\n";
        while let Some(pos) = src.chunk().windows(crlf.len()).position(|w| w.eq(crlf)) {
            let str = String::from_utf8(src.chunk()[..pos].to_vec()).unwrap();
            if first {
                if !str.to_lowercase().starts_with("get") {
                    return Err(Error::NotAPacket);
                }
                let mut space_pos = str.find(' ').unwrap();
                let str = &str[space_pos + 1..]; //skip command

                space_pos = str.find(' ').unwrap();
                if let Some(pos) = str[1..space_pos].find('/') {
                    if let Ok(idv) = str[1..pos+1].parse::<u32>() {
                        id = idv;
                        url = str[pos+1..].to_string();
                    }
                }
                first = false;
            } else {
                let h : Vec<&str> = str.split(":").collect();
                if h.len() == 2 {
                    let key = h.get(0).unwrap().to_string();
                    let val = h.get(1).unwrap().to_string();
                    headers.insert(key, val);
                }
            }
            src.advance(pos + 2);
        }

        return Ok(HttpData { server_id: id, url, headers })
    }
}

impl Command {
    pub fn parse(src:&mut Cursor<&[u8]>) -> Result<Command, Error> {
                
        let head = peek_b4(src)?;

        if head != PACKET_TAG {
            return Self::read_http_header(src);
        }

        src.advance(4);

        let cmd = decode_command(src)?;

        Ok(cmd)
    }

    fn read_http_header(src:&mut Cursor<&[u8]>) -> Result<Command, Error> {

        let crlf = b"\r\n\r\n";
        let pos = src.chunk().windows(crlf.len()).position(|w| w.eq(crlf));
        if pos.is_none() {
            return Err(Error::Incomplete);
        }
        
        let http_data = HttpData::from(src)?;

        return Ok(Command::HttpClientConnect(http_data));
    }

    pub async fn write(self : &Self, stream : &mut TcpStream) -> Result<(), io::Error> {
        let mut id = 0;
        let mut data_len :u32= 0;
        let mut str_holder : Option<Vec<u8>> = None;
        let mut data : Option<&Vec<u8>> = None;

        let cmd = match self {
            Self::ServerConnect(val) => {
                id = *val;
                SERVER_CONNECT
            }
            Self::ClienConnect(val) => {
                id = *val;
                CLIENT_CONNECT
            }
            Self::HttpClientConnect(val) => {
                id = val.server_id;
                CLIENT_CONNECT
            }
            Self::OK(_) => { OK_CMD }
            Self::Ping => { PING_CMD }
            Self::Close(val) => { 
                id = *val;
                CLOSE_CMD 
            }
            Self::Reject(str) => {
                let b = str.as_bytes();
                data_len = b.len() as u32;
                str_holder = Some(Vec::from(b));
                REJECT_CMD
            }
            Self::Data(dv) => {
                data_len = dv.len() as u32;
                data = Some(dv);
                DATA_CMD
            }
        };

        let tbuf = [
            PACKET_TAG.to_be_bytes(),
            cmd.to_be_bytes(),
            id.to_be_bytes(),
            data_len.to_be_bytes()
        ].concat();
        stream.write(&tbuf).await?;

        if data_len > 0 {
            if let Some(str_data) = str_holder {
                stream.write(&str_data).await?;
            } else {
                stream.write(&data.unwrap()).await?;
            }
        }

        Ok(())
    }
}

fn decode_command(src: &mut Cursor<&[u8]>) -> Result<Command, Error> {
    let cmd = get_u32(src)?;
    let id = get_u32(src)?; 
    let data_len = get_u32(src)?; 
    let data = get_buf(src, data_len as usize)?;


    match cmd {
        SERVER_CONNECT => { return Ok(Command::ServerConnect(id)) }
        CLIENT_CONNECT => { return Ok(Command::ClienConnect(id)) }
        OK_CMD => { return Ok(Command::OK(id)) }

        REJECT_CMD=> {
            return Ok(Command::Reject(str::from_utf8(&data.unwrap()).unwrap().to_string()))
        }

        PING_CMD => { return Ok(Command::Ping); }
        DATA_CMD => { return Ok(Command::Data(data.unwrap())); }
        CLOSE_CMD => { return Ok(Command::Close(id)); }

        _ => {
            Err(Error::UnexpectedCommand(cmd))
        }
    }
}

fn peek_b4(src: &Cursor<&[u8]>) -> Result<u32, Error> {
    if src.remaining() < 4 {
        return Err(Error::Incomplete);
    }

    let val = u32::from_be_bytes(src.chunk()[..4].try_into().unwrap());
    Ok(val)
}

fn get_u32(src: &mut Cursor<&[u8]>) -> Result<u32, Error> {
    if src.remaining() < 4 {
        return Err(Error::Incomplete);
    }

    let val = src.get_u32();
    Ok(val)
}

fn get_buf(src: &mut Cursor<&[u8]>, len : usize) -> Result<Option<Vec<u8>>, Error> {
    if len == 0 {
        return Ok(None);
    }

    if src.remaining() < len {
        return Err(Error::Incomplete);
    }

    let mut dest = Vec::with_capacity(len);
    dest.put_slice(&src.take(len).chunk()[..]);
    src.advance(len);

    Ok(Some(dest))
}