package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@ServerInfo(name="Decision")
@TableInfo(name="decision", keyFields="created")
public class Decision extends DataObject {
	public static int APPROVED = 1;
	public static int REJECTED = 2;

	public static int RET_DOC = 0;
	public static int ORD_DOC = 1;

	public Date created;
	public int value = 0;

	public int doctype = RET_DOC;
}
