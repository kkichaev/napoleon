package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="OrderProceededEx", keyFields="userid,type,created")
@ServerInfo(name="OrderProceeded")
public class OrderProceededEx extends OrderProceeded {
	public String userid = "";
	public String phone = "";
	public int params = 0;
	public int status = 0;
}
