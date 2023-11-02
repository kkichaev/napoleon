package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.TableInfo;

@TableInfo(name="CMonitoring", keyFields="created")
public class CMonitoring extends CreateDocDataObject {
	public String def = "";
	public List<CMonitoringItem> items = new ArrayList<CMonitoringItem>();
}
