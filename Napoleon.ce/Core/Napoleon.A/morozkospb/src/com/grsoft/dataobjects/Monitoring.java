package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="monitoring", keyFields="created")
public class Monitoring extends CreateDocDataObject{
	public List<MonitoringItem> items = new ArrayList<MonitoringItem>(); 
	public String suppl = "";
}
