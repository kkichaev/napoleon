package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="pricephotoserv", indexes="id")
@ServerInfo(name="PricePhotoServ")
public class PricePhotoServ extends DataObject{
	public String id = "";
	public String name = "";
	public String url = "";
}
