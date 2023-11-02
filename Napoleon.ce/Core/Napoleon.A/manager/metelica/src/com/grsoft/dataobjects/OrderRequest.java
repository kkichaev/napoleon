package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@ServerInfo(name="OrderRequest")
@TableInfo(name="orderrequest", keyFields="created")
public class OrderRequest extends CreateDocDataObject {
	public static int APPROVED = 0;
	public static int REJECTED = 1;
	public static int TOEDIT = 2;
	
	public Date order;
	public int decision;
}
