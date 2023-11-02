package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

import java.util.Date;

@TableInfo(name="lastscript", keyFields = "userid,created")
@ServerInfo(name="LastScript")
public class LastScript extends DataObject{
    public Date created;
    public String userid;
    public String orgid;
    public String orgname;
    public String scriptname;
    public int scriptid;
}
