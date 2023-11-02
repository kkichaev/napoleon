use tokio::io;
use tracing_subscriber::fmt;

use grjs::{handler};

#[tokio::main]
async fn main() -> io::Result<()> {

    let format = fmt::format()
        // .with_level(false) // don't include levels in formatted output
        // .with_target(false) // don't include targets
        // .with_thread_ids(true) 
        // .with_thread_names(true) 
        .with_ansi(false)
        .with_source_location(false)
        ;
 
    tracing_subscriber::fmt()
        .event_format(format)
        .with_env_filter("grjs=debug")
        // .with_max_level(Level::DEBUG)
        .init();

    handler::start("0.0.0.0:9595".to_string()).await
}
