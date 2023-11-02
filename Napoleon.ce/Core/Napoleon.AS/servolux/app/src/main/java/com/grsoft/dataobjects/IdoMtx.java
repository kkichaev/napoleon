package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="idomtx", keyFields="id,firm")
@ServerInfo(name="IdoMtx")
public class IdoMtx extends IdMtx {
	public static String CHANNEL_OBJ = "channel";
	public static String RETAIL_OBJ = "retail";
	public static String ORG_TYPE_OBJ = "orgtype";
	public static String ORG_OBJ = "org_obj";
	
	
	public String objectType = "";
}
