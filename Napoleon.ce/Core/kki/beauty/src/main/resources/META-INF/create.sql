/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  kkichaev
 * Created: 03.02.2021
 */
create table user(id bigint(20) not null primary key, login char(30), password char(30));
create table user_group(user_id int, group_id int);
create table groups(id int nut null primary key, name char(30));

