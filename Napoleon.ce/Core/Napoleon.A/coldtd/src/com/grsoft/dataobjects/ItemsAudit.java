package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="ItemsAudit", keyFields="created")
public class ItemsAudit extends CreateDocDataObject {
	public int orderCreated = 0;
	public List<ItemsAuditItem> items = new ArrayList<ItemsAuditItem>();
}
