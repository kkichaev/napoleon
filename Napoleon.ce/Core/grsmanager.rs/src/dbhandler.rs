use std::{sync::{Arc, Mutex}, str::FromStr, collections::HashSet, time::{SystemTime, Duration}, os::unix::net::UnixStream, io::{Read, Write}};
use std::time::UNIX_EPOCH;
use std::str;

use serde::{Serialize, Deserialize};
use serde_json::{Value, json};
use subprocess::{Exec, Popen, PopenConfig};
use tokio::{sync::mpsc::Receiver, fs, time::sleep, net::TcpStream};
use tokio_postgres::{self, NoTls, Client, Row};

use crate::{Config, error::{Commands, HandlerError, HandlerResult, HandlerResponse, ServerInfo, ConnectionInfo}, CommandType};
use tracing::debug;

#[derive(Serialize, Deserialize)]
pub struct ErrorResult {
    pub error: bool,
    pub message: String,
}

impl ErrorResult {
    pub fn make_error(msg: &str) -> String {
        let er = ErrorResult{error:true, message:String::from_str(msg).unwrap()};
        serde_json::to_string(&er).unwrap()
    }
}

async fn create_user_folder(base_folder:&str, cli_folder:&str, uid:&str, port:i32, socket:&str) -> HandlerResult {
    let full_folder = format!("{}{}", base_folder, cli_folder);
    let _ = fs::create_dir_all(full_folder.as_str()).await?;
    let _ = fs::create_dir(format!("{}/exp", full_folder.as_str())).await?;

    let config =format!(
"serverDefs = ../base.xml
addDefs = ../trade.xml
serverBase = GRServer.sdb
exchangeFolder = exp
imageFolder = exp
Port = {}
featuresFile = grserver.ftr
serverKey={}
httpSocket={}", port, uid, socket);

    let _ = fs::write(format!("{}/config.ini", full_folder.as_str()), config).await?;
    return Ok(HandlerResponse::ServerCode(uid.to_owned()));
}

fn gen_uid() -> String {
    let start = SystemTime::now();
    let since = start.duration_since(UNIX_EPOCH).unwrap().as_millis();

    let uid = format!("{:016x}", since ^ 0xFFFFFFFFFFFFFFF);
    if uid.len() == 16 {
        let split = [10,14,2,8,11,0,7,3,9,1,6,12,4,13,5,15];
        let mut val = [0u8; 16];
        
        let src = uid.as_bytes();
        let mut idx = 0;
        for i in split {
            val[idx] = src[i]; 
            idx += 1;
        }
        return String::from(str::from_utf8(&val).unwrap());
    }
    return uid;
}

fn get_userid(params: &Value) -> Result<i32, HandlerError> {
    let tuid = &params["userid"];
    if let Value::Number(eml) = tuid{
        if let Some(val) = eml.as_i64() {
            return Ok(val as i32);
        }
    } else if let Value::String(v) = tuid {
        if let Ok(res) = v.parse() {
            return Ok(res);
        }
    } 
    return Err(HandlerError::NoUserid)
}

async fn get_free_ports(cfg: Arc<Mutex<Config>>, client: &mut Client) -> Result<i32, HandlerError> {
    let mut used_ports:HashSet<u32> = HashSet::new();

    let rows = client.query("select port::integer from servers", &[]).await?;
    for p in rows {
        let fp : i32 = p.get(0);
        used_ports.insert(fp as u32);
    }

    {
        let cfg = cfg.lock().unwrap();
        for i in cfg.port_min..cfg.port_max {
            if !used_ports.contains(&i) {
                return Ok(i as i32);
            }
        }
    }

    return Err(HandlerError::NoFreePorts);
}

async fn create_server(cfg: Arc<Mutex<Config>>, client: &mut Client, params: &Value) -> HandlerResult {
    let base_cli_folder ;

    let free_port: i32 = get_free_ports(cfg.clone(), client).await?;
    let userid:i32 = get_userid(params)?;
    let mut name = "Server";

    if let Value::String(sn) = &params["name"] {
        name = sn;
    }

    {
        let cfg = cfg.lock().unwrap();
        base_cli_folder = String::from(&cfg.client_folder);
    }

    let uid = gen_uid();
    let token = &uid;//gen_uid();

    let client_folder = format!("{}/", uid);
    let socket = format!("/tmp/grs_{}.sock",&uid );

    let stmt = "insert into servers (port,folder,code,socket,userid,name,token,lazyStart) values ($1::integer,$2,$3,$4,$5::integer,$6,$7,0)";

    let _ires = client.query(stmt
        ,&[&free_port
        , &client_folder.as_str()
        , &uid.as_str()
        , &socket.as_str()
        , &userid
        , &name
        , &token
    ]).await?;
        
    let ret_str = create_user_folder(&base_cli_folder, &client_folder, &uid.as_str(), free_port, &socket).await?;
    
    // start server after creating
    let res = run_server(cfg.clone(), client, &uid, false).await;
    if res.is_err() {
        debug!("Error run server after creating {:?}", res.err());
    }

    for _ in 0..10 {
        debug!("Wait creating...");
        let tres = TcpStream::connect(("127.0.0.1",free_port as u16)).await;
        if tres.is_ok() {
            break;
        }
        sleep(Duration::from_millis(300)).await;
    }

    return Ok(ret_str);
}

fn is_port_occupied(port:i32) ->bool {
    let port_match = (String::from(":") + format!("{:04x}", port).as_str()).to_lowercase();

    let ports = { Exec::cmd("cat").arg("/proc/net/tcp") | Exec::cmd("awk").arg("{print $2}") }
        .capture()
        .unwrap()
        .stdout_str().to_lowercase();

    return ports.contains(port_match.as_str());
}

async fn find_server(db_client: &mut Client, fields:&str, token:&String, by_token:bool) -> Result<Vec<Row>, tokio_postgres::Error> {
    let param = match by_token {
        true => "token",
        false => "code"
    };
    let stmt = format!("select {} from servers where {}=$1", fields, param);
    return db_client.query(&stmt, &[&token]).await;
}

async fn start_server(port:i32, clients_folder:&str, server_folder:String, cli_folder:String) -> Result<(), HandlerError> {
    if !is_port_occupied(port) {
        let cli_folder = 
            if cli_folder.ends_with('/') { cli_folder } else { cli_folder + "/" };

        let mut args = Vec::new();
        args.push(server_folder + "grserver");
        args.push("--config-file".to_owned());
        args.push(format!("{}{}config.ini", clients_folder, cli_folder));

        debug!("Starting server on port {}", port);
        let mut prc = Popen::create(&args, PopenConfig::default())?;
        
        // let mut prc = Popen::create(&[
        //     server_folder + "grserver", "--config-file".to_owned(), format!("{}{}config.ini", clients_folder, cli_folder)
        //     ]
        //     , PopenConfig::default())?;
        prc.detach();
    }
    return Ok(())
}

// return server unix socket
async fn find_and_run_server(db_client: &mut Client, code:&String, by_token:bool, server_folder:String, clients_folder:String) -> HandlerResult {
    let res = find_server(db_client, "folder, port::integer, socket, code", code, by_token).await?;
    if res.len() == 0 {
        return Err(HandlerError::NoRecords);
    }

    let row = res.get(0).unwrap();
    let cli_folder:String = row.get(0);
    let port : i32 = row.get(1);
    let socket: String = row.get(2);
    let server_code: String = row.get(3);

    let _ = start_server(port, &clients_folder, server_folder, cli_folder).await?;

    // if !is_port_occupied(port) {
    //     if !cli_folder.ends_with("/") { cli_folder = cli_folder + "/"; }

    //     let mut prc = Popen::create(&[
    //         server_folder + "grserver", "--config-file".to_owned(), format!("{}{}config.ini", clients_folder, cli_folder)]
    //         , PopenConfig::default())?;
    //     prc.detach();
    // }
    return Ok(HandlerResponse::ServerSocket(socket, server_code));
}

async fn run_server(cfg: Arc<Mutex<Config>>, db_client: &mut Client, code: &String, by_token:bool) -> HandlerResult {
    let clients_folder;
    let server_folder = {
        let c = cfg.lock().unwrap();
        clients_folder = String::from(&c.client_folder);

        String::from_str(&c.server_folder).unwrap()
    };

    return find_and_run_server(db_client, code, by_token, server_folder, clients_folder).await;
}

async fn list_servers(cfg: Arc<Mutex<Config>>, client:&mut Client, userid:i32) -> HandlerResult {
    let addr = {
        let c = cfg.lock().unwrap();
        String::from_str(&c.server_address).unwrap()
    };

    let stmt = "select port::integer, code, name, token from servers where userid=$1::integer";
    let rows = client.query(stmt, &[&userid]).await?;
    
    let mut res: Vec<ServerInfo> = Vec::new();

    for p in rows {
        let port: i32 = p.get(0);
        let code: String  = p.get(1);
        let name: String = p.get(2);
        let token: String = p.get(3);

        res.push(ServerInfo { code, address: addr.clone(), port, name, token });
    }

    Ok(HandlerResponse::ServersList(res))
}

async fn set_blocked(client:&mut Client, uid:i32, block_servers:bool) -> HandlerResult {
    let stmt = "select port::integer, socket, code from servers where userid=$1";
    let rows = client.query(stmt, &[&uid]).await?;

    let mut block_payload = "[]".to_owned();
    if block_servers {
        let data = vec!["pda".to_owned(),"vanpda".to_owned()];
        block_payload = json!(data).to_string();
    }
    
    for (_, r) in rows.iter().enumerate() {
        let port:i32 = r.get(0);
        if is_port_occupied(port) {
            let socket:String = r.get(1);
            let code:String = r.get(2);

            let streamw = UnixStream::connect(socket);
            if streamw.is_ok() {
                let mut stream = streamw.unwrap();
                let body = format!(
"PUT /set_blocked HTTP/1.1\r
Host: localhost\r
Authorization: Bearer {}\r
Content-Type: application/json; charset=utf-8\r
Content-Length: {}\r\n\r\n{}"
                , code, block_payload.len(), block_payload);

                let _ = stream.write_all(body.as_bytes());
                loop {
                    let mut bf = [0; 2000];
                    let n = stream.read(&mut bf[..]);
                    if n.is_err() || n.unwrap() == 0 {
                        break;
                    }
                } 
            }        
        }
    }

    return Ok(HandlerResponse::Done);
}

async fn is_server_running(code:&str, client:&mut Client) -> HandlerResult {
    let stmt = "select port::integer from servers where code=$1";
    let rows = client.query(stmt, &[&code]).await?;

    let mut running = false;

    if rows.len() > 0 {
        let r = &rows[0];
        let port:i32 = r.get(0);

        running = is_port_occupied(port);
    }

    return Ok(HandlerResponse::ServerRunning(running));
}

async fn server_info(cfg: Arc<Mutex<Config>>, client:&mut Client, code:&str) -> HandlerResult {
    let (addr, clients_folder, server_folder) = {
        let c = cfg.lock().unwrap();
        (
            String::from_str(&c.server_address).unwrap(),
            String::from_str(&c.client_folder).unwrap(),
            String::from_str(&c.server_folder).unwrap()
        )
    };

    // let stmt = "select port::integer, code, name, folder, token, userid::integer, socket from servers where code=$1";
    let stmt = "select port::integer, code, name, folder, token from servers where code=$1";
    let rows = client.query(stmt, &[&code]).await?;
    
    if rows.len() == 0 {
        return Err(HandlerError::NoRecords);
    }

    let p = &rows[0];
    let port: i32 = p.get(0);
    let code: String  = p.get(1);
    let name: String = p.get(2);

    let cli_folder:String = p.get(3);
    let token:String = p.get(4);
    // let uid:i32 = p.get(5);
    // let socket:String = p.get(6);

    let _ = start_server(port, &clients_folder, server_folder, cli_folder).await?;

    return Ok(HandlerResponse::ServerInfo(ServerInfo{ code, address: addr.clone(), port, name, token }));
}

async fn token(client:&mut Client, code:&str, userid:i32, make_new:bool) -> HandlerResult {
    if make_new {
        let token = gen_uid();
        let stmt = "update servers set token=$1 where code=$2 and userid=$3::integer";
        let _ = client.query(stmt, &[&token, &code, &userid]).await?;
        return Ok(HandlerResponse::Token(token));
    }

    let stmt = "select token from servers where code=$1 and userid=$2::integer";
    let res = client.query(stmt, &[&code, &userid]).await?;
    if res.len() == 0 {
        return Err(HandlerError::NoRecords);
    }
    return Ok(HandlerResponse::Token(res[0].get(0)));
}

fn read_str(params:&Value, key:&str) -> Result<String, HandlerError> {
    if let Value::String(val) = &params[key] {
        return Ok(val.to_owned());
    }

    let msg = format!("No {} in params", key);
    Err(HandlerError::ErrorMessage(msg))
}

fn read_array(params:&Value, key:&str) -> Vec<i32> {
    let mut res = Vec::new();

    if let Value::Array(val) = &params[key] {
        for el in val {
            if let Value::Number(el) = &el {
                res.push(el.as_i64().unwrap() as i32);
            }
        }
    }

    return res;
}

fn read_bool(params:&Value, key:&str) -> bool {
    if let Value::Bool(val)= &params[key] {
        return val.to_owned();
    }
    if let Value::String(val) = &params[key] {
        let val = val.to_lowercase();
        return val == "true" || val == "1";
    }
    if let Value::Number(val) = &params[key] {
        let val =if val.is_f64() {val.as_f64().unwrap() as i32 
        } else if val.is_u64() { val.as_u64().unwrap() as i32 
        } else { val.as_i64().unwrap() as i32 };
        return val == 1;
    }
    return false;
}

fn read_int(params:&Value, key:&str) -> Result<i32, HandlerError> {
    if let Value::Number(val) = &params[key] {
        let val =if val.is_f64() {val.as_f64().unwrap() as i32 
        } else if val.is_u64() { val.as_u64().unwrap() as i32 
        } else { val.as_i64().unwrap() as i32 };
        return Ok(val);
    }

    if let Value::String(val) = &params[key] {
        let val = val.parse();
        if val.is_ok() {
            return Ok(val.unwrap());
        }
        let msg = format!("Error in value of the key {} {:?}", key, val.err());
        return Err(HandlerError::ErrorMessage(msg))
    }

    let msg = format!("No {} in params", key);
    Err(HandlerError::ErrorMessage(msg))
}

async fn update_server(cfg: Arc<Mutex<Config>>, client:&mut Client, code:&str, name:&str) -> HandlerResult {
    let stmt = "update servers set name=$1 where code=$2";
    let _ires = client.query(stmt, &[
        &name
        ,&code
    ]).await?;
    return server_info(cfg, client, code).await;
}

async fn run_command(cmd:Commands, cfg: Arc<Mutex<Config>>, db_client:&mut Client) -> HandlerResult {
    let answ = match cmd {
        Commands::CreateServer(params) => {
            create_server(cfg.clone(), db_client, &params).await?
        },

        Commands::RunServer(params) => {
            let code = read_str(&params, "code")?;
            run_server(cfg.clone(), db_client, &code, false).await?
        },

        Commands::GetServerSocket(token, by_token) => {
            run_server(cfg.clone(),db_client, &token ,by_token).await?
        },

        Commands::ServerList(params) => {
            let userid = read_int(&params, "userid")?;
            list_servers(cfg.clone(), db_client, userid).await?
        },

        Commands::SetBlocked(params) => {
            let bi = read_int(&params, "blocked")?;
            let uid = read_int(&params, "userid")?;
            set_blocked(db_client, uid, bi > 0).await?
        }

        Commands::IsServerRuning(server_code) => {
            is_server_running(&server_code, db_client).await?
        }

        Commands::ServerInfo(params) => {
            let code = read_str(&params, "code")?;
            server_info(cfg.clone(), db_client, &code).await?
        },

        Commands::UpdateServer(params) => {
            let code = read_str(&params, "code")?;
            let name = read_str(&params, "name")?;
            update_server(cfg.clone(), db_client, &code, &name).await?
        },

        Commands::ConnectionInfo(params) => {
            let code = read_str(&params, "code")?;
            let si = server_info(cfg.clone(), db_client, &code).await;
            if si.is_err() {
                return Err(si.err().unwrap())
            }
            if let HandlerResponse::ServerInfo(serv_info) = si.unwrap() {
                return Ok(HandlerResponse::ConnectionInfo(ConnectionInfo::from(serv_info, &cfg.lock().unwrap())))

            }
            return Err(HandlerError::ErrorMessage("unexpected ConnectionInfo".to_owned()))
        },

        Commands::Token(params) => {
            let code = read_str(&params, "code")?;
            let userid = read_int(&params, "userid")?;
            let make_new = read_bool(&params, "new");
        
            token(db_client, &code, userid, make_new).await?
        },
    };

    return Ok(answ);
}

async fn start_servers(cfg: Arc<Mutex<Config>>, db_client:&mut Client) {
    let stmt = "select code from servers where lazyStart <> 1";
    let res = db_client.query(stmt, &[]).await;

    if res.is_ok() {
        for r in res.unwrap() {
            let code:String = r.get(0);
            let r = run_server(cfg.clone(), db_client, &code, false).await;
            if r.is_err() {
                debug!("Start server {} error {:?}", code, r.err().unwrap());
            }
        }
    } else {
    }
}

async fn run_db(cfg: Arc<Mutex<Config>>, cmd_rx: &mut Receiver<CommandType>, starting_servers:bool) -> Result<(), HandlerError> {
    let connstr = {
        let cfg = cfg.lock().unwrap();
        cfg.connect_str()
    };

    let (mut db_client, connection) =
        tokio_postgres::connect(&connstr, NoTls).await?;

    // The connection object performs the actual communication with the database,
    // so spawn it off to run on its own.
    tokio::spawn(async move {
        if let Err(e) = connection.await {
            debug!("DB connection error: {}", e);
        }
    });

    if starting_servers {
        start_servers(cfg.clone(), &mut db_client).await;
    }

    loop {
        let cmd = cmd_rx.recv().await;
        if let Some(cmd) = cmd {

            let answ_tx = cmd.0;
            let res = run_command(cmd.1, cfg.clone(), &mut db_client).await;
            let _ = answ_tx.send(res);

        } else {
            debug!("DB cmd_rx None");
        }
    }
}

pub fn start_db_handler(cfg: Arc<Mutex<Config>>, mut cmd_rx:Receiver<CommandType>, starting_servers:bool) {
    
    tokio::spawn(async move {
        let cfg = cfg.clone();
        if let Err(err) = run_db(cfg, &mut cmd_rx, starting_servers).await {
            debug!("{:?}", err);

            // need to restart server
        }
    });
}