drop table if exists "AgentOrgs";
CREATE TABLE "AgentOrgs" ("id"  TEXT  ,"userid"  TEXT  , CONSTRAINT pk_AgentOrgs PRIMARY KEY ("userid","id"));
insert into "AgentOrgs" (userid, id) select userid, id from Org;


drop table if exists _tmp_dlv;
create table _tmp_dlv as select distinct "created","date","firm","id","number","payDate","sumD","ido" from Delivery;

drop table if exists _tmp_dlv_items;
create table _tmp_dlv_items as select distinct "id","qty","sum","Delivery$id","Delivery$number", party, expired from Delivery$items;


drop table if exists _tmp_orgs;
create table _tmp_orgs as select distinct "address","block","calcDebet","costype","formatTT","id","idChannel","ido",
"idRetailer","latitude","longitude","name","noDrop","stop" from Org;

drop table if exists _tmp_org_contacts;
create table _tmp_org_contacts as select distinct "id","name","phone","Org$id" from Org$contacts;

drop table if exists _tmp_org_sp;
create table _tmp_org_sp as select distinct "id","Org$id" from Org$salesPlaces;

alter table Delivery rename to DeliveryOld;
alter table Delivery$items rename to Delivery$itemsOld;
alter table Org rename to OrgOld;
alter table Org$contacts rename to Org$contactsOld;
alter table Org$salesPlaces rename to Org$salesPlacesOld;

CREATE TABLE "Delivery" ("created"  INTEGER  ,"date"  INTEGER  ,"firm"  TEXT  ,"id"  TEXT  ,"ido"  TEXT  ,"number"  TEXT  ,"payDate"  INTEGER  ,"sumD"  REAL  , 
CONSTRAINT pk_Delivery PRIMARY KEY ("id","number"));

insert or replace into Delivery ("created","date","firm","id","number","payDate","sumD","ido") select "created","date","firm","id","number","payDate","sumD","ido" from _tmp_dlv;


CREATE TABLE "Delivery$items" ("expired"  INTEGER  ,"id"  TEXT  ,"party"  TEXT  ,"qty"  REAL  ,"sum"  REAL  ,"Delivery$id"  TEXT  ,"Delivery$number"  TEXT  , 
CONSTRAINT fk_Delivery$items FOREIGN KEY ("Delivery$id","Delivery$number") REFERENCES "Delivery" ("id","number") ON DELETE CASCADE);
DROP INDEX if exists fki_Delivery$items;
CREATE INDEX fki_Delivery$items ON "Delivery$items" ("Delivery$id","Delivery$number");

insert or replace into "Delivery$items" ("id","qty","sum","expired","Delivery$id","Delivery$number") select "id","qty","sum","expired","Delivery$id","Delivery$number" from _tmp_dlv_items;



CREATE TABLE "Org" ("address"  TEXT  ,"block"  INTEGER  ,"calcDebet"  INTEGER  ,"costype"  INTEGER  ,"formatTT"  TEXT  ,"id"  TEXT  ,
"idChannel"  TEXT  ,"ido"  TEXT  ,"idRetailer"  TEXT  ,"latitude"  REAL  ,
"longitude"  REAL  ,"name"  TEXT  ,"noDrop"  INTEGER  ,"stop"  INTEGER  ,"userid"  TEXT  , CONSTRAINT pk_Org PRIMARY KEY ("id"));

insert or replace into "Org" ("address","block","calcDebet","costype","formatTT","id","idChannel","ido", "idRetailer","latitude","longitude","name","noDrop","stop") 
select "address","block","calcDebet","costype","formatTT","id","idChannel","ido","idRetailer","latitude","longitude","name","noDrop","stop" from _tmp_orgs;


CREATE TABLE "Org$contacts" ("id"  TEXT  ,"name"  TEXT  ,"phone"  TEXT  ,"Org$id"  TEXT  , CONSTRAINT fk_Org$contacts FOREIGN KEY ("Org$id") REFERENCES "Org" ("id") ON DELETE CASCADE);
DROP INDEX if exists fki_Org$contacts;
CREATE INDEX fki_Org$contacts ON "Org$contacts" ("Org$id");

insert or replace into "Org$contacts" ("id","name","phone","Org$id") select "id","name","phone","Org$id" from _tmp_org_contacts;

CREATE TABLE "Org$salesPlaces" ("id"  TEXT  ,"Org$id"  TEXT  , CONSTRAINT fk_Org$salesPlaces FOREIGN KEY ("Org$id") REFERENCES "Org" ("id") ON DELETE CASCADE);
DROP INDEX if exists fki_Org$salesPlaces;
CREATE INDEX fki_Org$salesPlaces ON "Org$salesPlaces" ("Org$id");

insert or replace into "Org$salesPlaces" ("id","Org$id") select "id","Org$id" from _tmp_org_sp;

