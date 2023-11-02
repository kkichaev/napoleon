DROP DATABASE IF EXISTS jdbctest;
CREATE DATABASE jdbctest;

use jdbctest;


create table users(
	user_id integer(10) not null,
	username varchar(10) not null,
	first_name varchar(15),
	middle_name varchar(15),
	last_name varchar(20),
	password varchar(32) not null,
	primary key(user_id)
);

create table `groups`(
	group_id integer(10) not null,
	group_name varchar(20) not null,
	group_desc varchar(200) not null,
	primary key(group_id)
);

create table user_groups(
	user_id integer(10) not null,
	group_id integer(100) not null,
	primary key (user_id, group_id),
	constraint user_groups_user_fk foreign key(user_id) references users (user_id),
	constraint user_groups_group_fk foreign key(group_id) references `groups` (group_id)
);