package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="planogramdef", keyFields="id")
@ServerInfo(name="PlanogramDef")
public class PlanogramDef extends DataObject {
	public String id = "";
	public List<PlanogramDefItem> items = new ArrayList<PlanogramDefItem>();
}
