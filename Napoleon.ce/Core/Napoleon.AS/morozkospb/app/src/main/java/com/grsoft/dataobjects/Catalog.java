package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@ServerInfo(name="Catalog")
@TableInfo(name="catalog", keyFields="id")
public class Catalog extends DataObject {
	public String id = "";
	public String idCompany = "";
	public String name = "";
	public Date start;
	public Date finish;
}
