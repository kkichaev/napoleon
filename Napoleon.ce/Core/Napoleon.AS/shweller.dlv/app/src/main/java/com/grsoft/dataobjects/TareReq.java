package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

@TableInfo(name="TareReq", keyFields = "id,number")
@ServerInfo(name="ReqTareDoc")
public class TareReq extends Waybill {
}
