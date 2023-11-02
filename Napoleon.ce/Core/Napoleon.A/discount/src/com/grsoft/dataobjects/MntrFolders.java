package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="MonitoringFolders", keyFields="fid")
@ServerInfo(name="MonitoringFolders")
public class MntrFolders extends DataObject {
	public int id;
	public int level;
	public String fid = "";
	public String name = "";
}
