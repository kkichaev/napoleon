package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="recommend", keyFields="orgid,priceid", indexes="orgid")
@ServerInfo(name="Recommend")
public class Recommend extends DataObject {
	public String orgid = "";
	public String priceid = "";
}
