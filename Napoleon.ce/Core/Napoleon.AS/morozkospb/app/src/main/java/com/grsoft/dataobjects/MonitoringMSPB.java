package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="monitoring", keyFields="created")
public class MonitoringMSPB extends CreateDocDataObject{
	public List<MonitoringItemMSPB> items = new ArrayList<MonitoringItemMSPB>();
	public String suppl = "";
}
