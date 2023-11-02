package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="RetailCode", indexes="id")
@ServerInfo(name="RetailCode")
public class RetailCode extends DataObject {
	public String id = "";
	public String id_i = "";
	public String code = "";
}
