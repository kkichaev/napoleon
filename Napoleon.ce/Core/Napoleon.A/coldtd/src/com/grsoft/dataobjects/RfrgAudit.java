package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="OrgRfrg", keyFields="created")
public class RfrgAudit extends CreateDocDataObject {
	public int exclusive;
	
	public List<RfrgAuditItem> items = new ArrayList<RfrgAuditItem>();
}
