package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="concurent", keyFields="id")
@ServerInfo(name="Concurent")
public class Concurent extends DataObject {
	public String name = "";
	public String id = "";
}
