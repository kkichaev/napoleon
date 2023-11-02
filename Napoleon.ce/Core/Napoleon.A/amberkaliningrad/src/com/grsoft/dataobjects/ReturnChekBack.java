package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="ReturnChekBack", keyFields="created")
@ServerInfo(name="ReturnChekBack")
public class ReturnChekBack extends ChekBase {
	public Date chek = new Date();
}
