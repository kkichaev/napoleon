package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.TableInfo;

@TableInfo(name="inventoryaudit", keyFields="created")
public class InvAudit extends CreateDocDataObject{
	public List<InvAuditItem> items = new ArrayList<InvAuditItem>();
}
