package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="dsc_mon", keyFields = "created")
@ServerInfo(name="MonitoringDoc")
public class DiscountMonitoring extends CreateDocDataObject {
	public List<DiscountMonitoringItem> items = new ArrayList<DiscountMonitoringItem>();
}
