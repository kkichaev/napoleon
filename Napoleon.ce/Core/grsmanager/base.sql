create table servers (id serial primary key, port int, folder varchar (500), code varchar(20), socket varchar(500), email varchar(100), lazyStart int); 
create unique index server_u on servers(code);
create index server_email on servers(email);