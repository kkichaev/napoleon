create table ord_temp as select * from "Order" where created > (11644041600 - 3600 * 24 * 60 + strftime('%s','now'))  * 10000000;
create table ord_temp$items as select * from "Order$items" where Order$created > (11644041600 - 3600 * 24 * 60 + strftime('%s','now'))  * 10000000;

create table distr_temp as select * from "DistribGroupDoc" where created > (11644041600 - 3600 * 24 * 60 + strftime('%s','now'))  * 10000000;
create table distr_temp$items as select * from "DistribGroupDoc$items" where DistribGroupDoc$created > (11644041600 - 3600 * 24 * 60 + strftime('%s','now'))  * 10000000;

.output copy.sql
.dump ord_temp%
.dump distr_temp%
.dump Division%
.dump DistribGroup
.dump DistribGroup$items
.dump OrgFolder%
