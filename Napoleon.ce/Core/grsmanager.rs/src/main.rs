use std::{ sync::{Arc, Mutex}, env};
use grsmanager::{fcgi::start_fcgi_handler, cmdhandler::{start_cmd_handler}, Config, dbhandler::start_db_handler};
use tokio::{sync::mpsc::{self}};
use tracing_subscriber::EnvFilter;

#[tokio::main]
async fn main() {

    let format = tracing_subscriber::fmt::format()
        // .with_level(false) // don't include levels in formatted output
        // .with_target(false) // don't include targets
        // .with_thread_ids(true) 
        // .with_thread_names(true) 
        .with_ansi(false)
        .with_source_location(false)
        ;
        
    tracing_subscriber::fmt()
        .event_format(format)
        .with_env_filter(EnvFilter::from_env("GRMANAGER_LOG"))
        // .with_env_filter("grsmanager=debug")
        // .with_max_level(Level::DEBUG)
        .init();

    let (db_tx, db_rx) = mpsc::channel(10);

    let cfg = Config::read("./grsmanager.ini").unwrap();
    let port = cfg.fcgi_port;
    let command_mutex = Arc::new(tokio::sync::Mutex::new(0));

    start_cmd_handler(&cfg, db_tx.clone(), command_mutex.clone());
    
    let mut starting_servers = false;
    for a in env::args() {
        if a.eq_ignore_ascii_case("start_servers") {
            starting_servers = true;
        }
    }

    let prefix = String::from(&cfg.fcgi_page_prefix);
    let cfg = Arc::new(Mutex::new(cfg));
    start_db_handler(cfg.clone(), db_rx, starting_servers);

    // debug!("Starting on {}", val);

    // let _ = fs::remove_file(val.clone());
    // let listener = UnixListener::bind(val.clone()).unwrap();
    // fs::set_permissions(val, fs::Permissions::from_mode(0o666)).unwrap();

    let addr = format!("127.0.0.1:{}", port);
    start_fcgi_handler(addr, prefix, db_tx.clone(), command_mutex).await;
}
