package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

import java.util.Date;

@TableInfo(name = "ChekResponse", keyFields = "created")
@ServerInfo(name="ChekResponse")
public class CheckResponse extends DataObject{
    public Date created = new Date();
    public String fsign = "";
    public String fdrv = "";
    public String fdoc = "";

    public String link = "";
}
