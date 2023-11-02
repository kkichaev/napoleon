use std::fmt;

use serde::Serialize;
use serde_json::{json, Value};
use subprocess::PopenError;

use crate::Config;


const CREATE_CMD: &str = "create_server";
const RUN_CMD: &str = "run_server";
const LIST_CMD: &str = "server_list";
const SERVER_INFO_CMD:&str = "server_info";
const TOKEN_CMD:&str = "token";
const CONNECTION_INFO_CMD:&str = "connection_info";
const UPDATE_SERVER:&str = "update_server";
const SET_BLOCKED_CMD: &str = "set_blocked";

pub enum Commands {
    CreateServer(Value),
    RunServer(Value),
    ServerList(Value),          // userid
    ServerInfo(Value),          // server_code 
    Token(Value),               // server_code 
    ConnectionInfo(Value),      // server_code 
    UpdateServer(Value),        // server_code 
    SetBlocked(Value),          // blocked [0,1], userid

    IsServerRuning(String),     // server_code
    GetServerSocket(String, bool),   // token for full access, bool - isToken
}

#[derive(Debug)]
pub enum HandlerResponse {
    Done,
    ServerSocket(String, String), // unix socket to server
    ServerCode(String),
    ServersList(Vec<ServerInfo>),
    ServerInfo(ServerInfo),
    Token(String),
    ConnectionInfo(ConnectionInfo),
    
    ServerRunning(bool),
}

impl Commands {
    pub fn from(cmd:&str, params:Option<Value>) -> Option<Commands> {
        let need_params = [CREATE_CMD, RUN_CMD, LIST_CMD, SERVER_INFO_CMD, TOKEN_CMD, UPDATE_SERVER, SET_BLOCKED_CMD];
        if params.is_none() && need_params.contains(&cmd) {
            return None;
        }

        return match cmd {
            CREATE_CMD => Some(Commands::CreateServer(params.unwrap())),
            RUN_CMD => Some(Commands::RunServer(params.unwrap())),
            LIST_CMD => Some(Commands::ServerList(params.unwrap())),
            SERVER_INFO_CMD => Some(Commands::ServerInfo(params.unwrap())),
            TOKEN_CMD => Some(Commands::Token(params.unwrap())),
            UPDATE_SERVER => Some(Commands::UpdateServer(params.unwrap())),
            CONNECTION_INFO_CMD => Some(Commands::ConnectionInfo(params.unwrap())),
            SET_BLOCKED_CMD => Some(Commands::SetBlocked(params.unwrap())),
            _ => None,
        }
    }
}

#[derive(Debug)]
pub enum HandlerError {
    Postgres(tokio_postgres::Error),
    Popen(PopenError),
    ErrorMessage(String),
    RcvHandler(tokio::sync::oneshot::error::RecvError),
    SendCmd(tokio::sync::mpsc::error::SendError<Commands>),
    IO(std::io::Error),
    FCGI(tokio_fastcgi::Error),
    NoRecords,
    NoUserid,
    NoFreePorts,
}

pub type HandlerResult = Result<HandlerResponse, HandlerError>;

impl std::fmt::Debug for Commands {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        let msg = format!("Command {:?}", self);
        f.write_str(&msg)
    }
}

#[derive(Serialize,Debug)]
pub struct ServerInfo {
    pub code : String,
    pub address: String,
    pub port: i32,
    pub name: String,
    pub token: String, 
}

#[derive(Serialize,Debug)]
pub struct ConnectionInfo {
    pub code : String,
    pub address: String,
    pub port: i32,

    pub bucket: String,
    pub bucket_key_id: String,
    pub bucket_key: String,
    pub bucket_region: String,
    pub bucket_host: String,
}

impl ConnectionInfo {
    pub fn from(si:ServerInfo, cfg:&Config) -> Self {
        ConnectionInfo { code:si.code, address: si.address, port: si.port
            , bucket: cfg.bucket.to_owned()
            , bucket_key_id:cfg.bucket_key_id.to_owned()
            , bucket_key: cfg.bucket_key.to_owned()
            , bucket_region: cfg.bucket_region.to_owned()
            , bucket_host: cfg.bucket_host.to_owned() } 
    }
}

pub fn make_server_answer(response:bool, message:&str) -> Value {
    let res= match response { true => 1, false => 0 };
    return json!({"name":"ServerAnswer", "data":[{"response": res, "message" : message}]});
}

impl HandlerError {
    pub fn to_response(&self) ->String {
        let msg = format!("{:?}", self);
        return json!([make_server_answer(false, &msg)]).to_string();
    }
}

impl HandlerResponse {
    fn make_data(name:&str, data:Value) -> Value {
        if let Value::Array(_) = data {
            json!({"name":name, "data":data})
        } else {
            json!({"name":name, "data":[data]})
        }
    }

    pub fn to_response(&self) -> String {
        let data_obj= match self {
            HandlerResponse::ServerCode(code) => 
                HandlerResponse::make_data("ServerCode", json!({"code":code})),
            HandlerResponse::ServerSocket(socket, _) => 
                HandlerResponse::make_data("ServerSocket", json!({"socket":socket})),
            HandlerResponse::Token(token) => 
                HandlerResponse::make_data("Token", json!({"token":token})),
            HandlerResponse::ServerRunning(running) =>
                HandlerResponse::make_data("ServerStatus", json!({"running":if *running {1} else {0} })),

            HandlerResponse::ServersList(list) => {
                let v = serde_json::to_value(list);
                let data = if v.is_err() {
                    serde_json::Value::Null
                } else {
                    v.unwrap()
                };

                HandlerResponse::make_data("ServersList", data)
            },
            HandlerResponse::ServerInfo(si) => {
                let v = serde_json::to_value(si);
                let data = if v.is_err() {
                    serde_json::Value::Null
                } else {
                    v.unwrap()
                };

                HandlerResponse::make_data("ServerInfo", data)
            },
            HandlerResponse::ConnectionInfo(si) => {
                let v = serde_json::to_value(si);
                let data = if v.is_err() {
                    serde_json::Value::Null
                } else {
                    v.unwrap()
                };

                HandlerResponse::make_data("ServerConnection", data)
            },
            HandlerResponse::Done => {
                HandlerResponse::make_data("Done", json!({"result":1}))
            }
        };

        return json!([make_server_answer(true, ""), data_obj]).to_string();
    }
}

impl From<tokio_fastcgi::Error> for HandlerError {
    fn from(value: tokio_fastcgi::Error) -> Self {
        HandlerError::FCGI(value)
    }
}

impl From<std::io::Error> for HandlerError {
    fn from(value: std::io::Error) -> Self {
        HandlerError::IO(value)
    }
}

impl From<tokio::sync::mpsc::error::SendError<Commands>> for HandlerError {
    fn from(value: tokio::sync::mpsc::error::SendError<Commands>) -> Self {
        HandlerError::SendCmd(value)
    }
}

impl From<tokio::sync::oneshot::error::RecvError> for HandlerError {
    fn from(value: tokio::sync::oneshot::error::RecvError) -> Self {
        HandlerError::RcvHandler(value)
    }
}

impl From<tokio_postgres::Error> for HandlerError {
    fn from(value: tokio_postgres::Error) -> Self {
        HandlerError::Postgres(value)
    }
}

impl From<PopenError> for HandlerError {
    fn from(value: PopenError) -> Self {
        HandlerError::Popen(value)
    }
}

impl fmt::Display for HandlerResponse {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        let msg = match self {
            HandlerResponse::ServerSocket(sock, _) => sock.to_owned(),                        
            HandlerResponse::ServerCode(code) => code.to_owned(),
            HandlerResponse::Token(code) => code.to_owned(),
            HandlerResponse::Done => "ok".to_owned(),
            HandlerResponse::ServerRunning(running) => if *running {"1".to_owned()} else {"0".to_owned()},
            
            HandlerResponse::ServersList(src) => {
                let v = serde_json::to_value(src);
                if v.is_err() {
                    "".to_owned()
                } else {
                    v.unwrap().to_string()
                }
            },

            HandlerResponse::ServerInfo(src) => {
                let v = serde_json::to_value(src);
                if v.is_err() {
                    "".to_owned()
                } else {
                    v.unwrap().to_string()
                }
            },

            HandlerResponse::ConnectionInfo(src) => {
                let v = serde_json::to_value(src);
                if v.is_err() {
                    "".to_owned()
                } else {
                    v.unwrap().to_string()
                }
            },
        };
        write!(f, "{}", msg)
    }
}
