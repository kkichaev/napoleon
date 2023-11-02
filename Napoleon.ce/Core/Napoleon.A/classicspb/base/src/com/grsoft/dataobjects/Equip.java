package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="equip", keyFields="id", indexes="ido")
@ServerInfo(name="Equip")
public class Equip extends DataObject {
	public String number = "";
	public String name = "";
	public String id = "";
	public String ido = "";
	public String barcode = ""; 
}
