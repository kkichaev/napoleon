package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="OrgActions", keyFields="ido,id", indexes="id")
public class OrgAction extends DataObject {
	public String id = "";
	public String ido = "";
	
	public List<OrgActionItem> items = new ArrayList<OrgActionItem>();
}
