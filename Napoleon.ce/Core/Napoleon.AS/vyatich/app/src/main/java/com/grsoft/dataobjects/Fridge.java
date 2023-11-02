package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="fridge", keyFields="id", indexes="ido")
@ServerInfo(name="Fridge")
public class Fridge extends DataObject {
	public String number = "";
	public String name = "";
	public String id = "";
	public String ido = "";
	public String barcode = "";
}
