package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="DivisionManager", keyFields="login")
@ServerInfo(name="IAMDivisionManager")
public class DivisionManager extends DataObject {
	public String login = "";
	public int mobile = 0;
}
