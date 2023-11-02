package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.database.DbReader;
import com.grsoft.database.TableInfo;

import java.util.Date;

@TableInfo(name="_ServerInfo")
@com.grsoft.database.ServerInfo(name="%ServerInfo")
public class ServerInfo extends DataObject {
    public String name = "";
    public Date time = new Date();
    public Date received = new Date();
    public int timeZone = 0;

    public static ServerInfo read() {
        ServerInfo ret = new ServerInfo();

        DbReader r = new DbReader();
        r.select(ret, ret.getTableName(), "");
        r.close();

        return ret;
    }
}
