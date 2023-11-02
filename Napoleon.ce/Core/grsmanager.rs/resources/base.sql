create table servers (
    id serial primary key
    , port int NOT NULL
    , folder varchar (500) NOT NULL
    , code varchar(50) NOT NULL
    , socket varchar(500) NOT NULL
    , userid int NOT NULL
    , name varchar(150) NOT NULL
    , token varchar(50) NOT NULL
);
 
create unique index server_u on servers(code);
create unique index server_t on servers(token);
create index server_userid on servers(userid);

--insert into servers(port,folder,code,socket,userid) values (3000,'18535a8e959','18535a8e959','/tmp/grs_18535a8e959.sock',1);

