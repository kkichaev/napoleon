package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@ServerInfo(name="WHouses")
@TableInfo(name="whouses", keyFields="id")
public class WHouses extends DataObject {
	public int id = 0;
	public String name = "";
}
