package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="fridge", indexes="id")
@ServerInfo(name="Fridge")
public class Fridge extends DataObject{
	public String id = "";
	public String number = "";
}
