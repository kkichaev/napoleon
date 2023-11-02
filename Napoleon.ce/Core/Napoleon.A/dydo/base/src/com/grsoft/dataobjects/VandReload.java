package com.grsoft.dataobjects;

import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="VandReload", keyFields="created")
public class VandReload extends CreateDocDataObject {
	public Date linkedAudit;
	public List<VandReloadItem> items;
	public String agentSklad = "";
}
