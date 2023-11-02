package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="Slsnet", keyFields="id")
@ServerInfo(name="Slsnet")
public class Slsnet extends DataObject {
	public String id = "";
	public int plan = 0;
	public String name = "";
}
