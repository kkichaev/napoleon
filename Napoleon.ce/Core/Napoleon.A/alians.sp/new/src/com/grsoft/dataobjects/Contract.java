package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="contract", keyFields="id")
@ServerInfo(name="Contract")
public class Contract extends DataObject {
	public String id = "";
	public String name = "";
	
	public List<ContractItem> items = new ArrayList<ContractItem>();
	
	@Override
	public String toString() {
		return name;
	}
}
