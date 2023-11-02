package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="KAgents", indexes="ido")
@ServerInfo(name="Contragent")
public class KAgents extends DataObject {
	public String ido = "";
	public String id = "";
	public String name = "";

	@Override public String toString() { return name; }
}
