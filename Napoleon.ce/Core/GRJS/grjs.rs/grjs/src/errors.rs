use std::{fmt::Display, io};


#[derive(Debug)]
pub enum Error {
    UndefCommand,
    ConnectionReset,
    IOError(io::Error),
}

impl std::error::Error for Error {}

impl Display for Error {
    fn fmt(&self, _: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        Ok(())
    }
}

