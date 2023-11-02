package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="OrgDiscount", keyFields="id")
@ServerInfo(name="OrgDiscount")
public class OrgDiscount extends DataObject {
	public String id = "";
	public List<OrgDiscountItem> items = new ArrayList<OrgDiscountItem>();
}
