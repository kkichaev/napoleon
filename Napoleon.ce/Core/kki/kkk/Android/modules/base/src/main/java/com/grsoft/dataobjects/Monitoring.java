package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="Monigoring",keyFields="created")
public class Monitoring extends CreateDocDataObject {
	public List<MonitoringDocItem> items;
}
