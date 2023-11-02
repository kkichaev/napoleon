package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name = "autoinfo", keyFields = "id")
@ServerInfo(name="AutoInfo")
public class AutoInfo extends DataObject{
    public String id = "";
    public String number = "";
    public int color;
    public String fuel1Id = "";
    public String fuel2Id = "";
}
