package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="contractdef",keyFields="id")
@ServerInfo(name="ContractDef")
public class ContractDef extends DataObject {
	public String id = "";
	public String name = "";
	public Date start = new Date();
	public Date finish = new Date();
	
	public List<ContractDefItem> items = new ArrayList<ContractDefItem>();
}
