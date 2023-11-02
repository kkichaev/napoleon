package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

import java.util.Date;

@TableInfo(name="MemoStatus", keyFields = "created", indexes = "status")
@ServerInfo(name="MemoStatus")
public class MemoStatus extends DataObject{
    public Date created;
    public String status = "";
}
