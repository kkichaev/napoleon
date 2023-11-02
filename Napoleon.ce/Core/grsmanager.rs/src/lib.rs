
use core::fmt;
use std::env::{self};

use error::{HandlerResult, Commands};
use ini::Ini;
use tokio::sync::oneshot;

#[derive(Debug)]
pub struct Config {
    pub fcgi_port :u32,
    pub cmd_socket: String,

    pub db_server : String,
    pub db_port: String,
    pub database: String,
    pub db_user: String,
    pub db_password: String,

    pub fcgi_page_prefix:String,
    pub server_folder:String,
    pub client_folder:String,

    pub server_address:String,

    pub port_min:u32,
    pub port_max:u32,

    pub bucket: String,
    pub bucket_key_id: String,
    pub bucket_key:String,
    pub bucket_region:String,
    pub bucket_host:String,
}

type CommandType = (oneshot::Sender<HandlerResult>,Commands);

#[derive(Debug)]
pub enum Error {
    IniError(ini::Error),
    VarError(env::VarError),
    PGError(tokio_postgres::Error)
}

impl fmt::Display for Error {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::IniError(err) => { write!(f, "{}", err) },
            Self::VarError(err) => { write!(f, "{}", err) },
            Self::PGError(err) => { write!(f, "{}", err) },
        }
    }
}

impl From<env::VarError> for Error {
    fn from(value: env::VarError) -> Self {
        Error::VarError(value)
    }
}

impl From<ini::Error> for Error {
    fn from(value: ini::Error) -> Self {
        Error::IniError(value)
    }
}

impl From<tokio_postgres::Error> for Error {
    fn from(value: tokio_postgres::Error) -> Self {
        Error::PGError(value)
    }
}

impl Config {
    pub fn connect_str(&self) -> String {
        return format!("postgresql://{}{}@{}{}{}"
            ,self.db_user
            ,self.db_password
            ,self.db_server
            ,self.db_port
            ,self.database);
    }

    fn parse_value(v: &str) -> String {
        if v.starts_with('$') {
            if let Ok(val) = env::var(v[1..].to_owned()) {
                return val
            }
        }
        v.to_owned()
    }

    fn to_full_path(v: &str) -> String {
        if v.starts_with('~') || v.starts_with('/') {
            return v.to_owned();
        }
        let dir =env::current_dir().unwrap();
        let mut dir = dir.to_str().unwrap().to_owned();
        if !dir.ends_with('/') {
            dir = dir + "/";
        }
        return format!("{}{}",dir, v);
    }

    pub fn read(filename:&str) -> Result<Config, Error> {
        let v = Ini::load_from_file(filename)?;

        let mut db_server= String::from("");
        let mut database= String::from("");
        let mut client_folder= String::from("");
        let mut db_port = String::from("");
        let mut db_password= String::from("");
        let mut db_user= String::from("");
        let mut server_folder= String::from("");

        let cmd_socket= env::var("GRS_CMD_SOCK")?;
        let fcgi_port = env::var("GRS_FCGI_PORT").unwrap().parse().unwrap();
        let fcgi_page_prefix = "/".to_string() + &env::var("GRS_PAGE_PREFIX")?;

        let mut port_min = 0;
        let mut port_max = 0;

        let bucket = env::var("BUCKET").unwrap_or("data.napmobile.ru".to_owned());
        let bucket_key_id = env::var("BUCKET_KEY_ID").unwrap_or("YCAJEeesm_2BKHxmOjybS5VBh".to_owned());
        let bucket_key= env::var("BUCKET_KEY").unwrap_or("YCN0bLoO8JZSBUblMqJ3T2SybAjVrUxrZvfGWtmn".to_owned());
        let bucket_region= env::var("BUCKET_REGION").unwrap_or("ru-central1".to_owned());
        let bucket_host= env::var("BUCKET_HOST").unwrap_or("storage.yandexcloud.net".to_owned());
    
        let mut server_address = String::from("");

        for (_, prop) in v.iter() {
            for (k, v) in prop.iter() {
                match k {
                    "dbServer" => { db_server = Config::parse_value(v)},
                    "dbName" => { database = "/".to_owned() + Config::parse_value(v).as_str()},
                    "dbUser" => { db_user = Config::parse_value(v)},
                    "dbPassword" => { db_password = ":".to_owned() + Config::parse_value(v).as_str()},
                    "serverFolder" => { 
                        server_folder = Config::to_full_path(v);
                        if !server_folder.ends_with("/") { server_folder = server_folder + "/" }
                    },
                    "clientsFolder" => { 
                        client_folder = Config::to_full_path(v);
                        if !client_folder.ends_with("/") { client_folder = client_folder + "/" }
                    },
                    "dbPort" => { db_port = ":".to_owned() + Config::parse_value(v).as_str()},
                    "portMin" => { port_min = Config::parse_value(v).parse().unwrap()},
                    "portMax" => { port_max = Config::parse_value(v).parse().unwrap()},
                    "serverAddress" => { server_address = Config::parse_value(v); }
                    _ =>{},
                }
            }
        }

        return Ok(Config{client_folder, cmd_socket, database, db_password, db_port, db_server
            , db_user, fcgi_page_prefix, fcgi_port, server_folder, port_min, port_max, server_address
            , bucket, bucket_key_id, bucket_key, bucket_region, bucket_host});

    }
}

pub mod fcgi;
pub mod cmdhandler;
pub mod dbhandler;
pub mod error;