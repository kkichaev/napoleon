package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

import java.util.Date;

@TableInfo(name="OrgSchedule", keyFields="key", indexes = "date")
@ServerInfo(name="OrgSchedule")
public class OrgSchedule extends DataObject{

    public static final int EXPORTED = 1;

    public String key = "";
    public String userid = "";
    public String id = "";
    public Date date;
    public String remark = "";
    public int params = 0;
}
