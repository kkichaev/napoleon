package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="typedistrib", keyFields="id")
@ServerInfo(name="TypeDistrib")
public class TypeDistrib extends DataObject {
	public String id = "";
	public String text = "";
}
