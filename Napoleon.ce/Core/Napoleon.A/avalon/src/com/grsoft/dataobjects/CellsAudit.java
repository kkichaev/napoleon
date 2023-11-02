package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="CellsAudit", keyFields="created")
public class CellsAudit extends CreateDocDataObject {
	public List<CellsAuditItem> items;
}
