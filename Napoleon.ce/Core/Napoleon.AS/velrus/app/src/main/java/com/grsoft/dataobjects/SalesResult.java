package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;

@ServerInfo(name="SalesResult")
public class SalesResult extends DataObject {
    public int status = 0;
    public String message = "";
}
