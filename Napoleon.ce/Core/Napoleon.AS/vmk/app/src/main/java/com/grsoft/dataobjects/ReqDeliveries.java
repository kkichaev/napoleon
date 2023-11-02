package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;

@ServerInfo(name = "ReqDeliveries")
public class ReqDeliveries extends DataObject {
    public ReqDeliveries(String id) {
        orgId = id;
    }

    public String orgId = "";
}
