create table tmp_log as select * from userlog;
drop table userlog;
CREATE TABLE "UserLog" ("action"  INTEGER  ,"category"  INTEGER  ,"comments"  TEXT  ,"date"  INTEGER  ,"objDate"  INTEGER  ,"objType"  TEXT  ,"unixtime"  REAL  ,"userid"  TEXT  , CONSTRAINT pk_UserLog PRIMARY KEY ("userid","unixtime"));
CREATE INDEX "UserLog_date" ON "UserLog" ("date");
insert into "UserLog" ("action","category","comments","date","objDate","objType","userid","unixtime") select "action","category","comments","date","objDate","objType","userid",rowid from tmp_log;
