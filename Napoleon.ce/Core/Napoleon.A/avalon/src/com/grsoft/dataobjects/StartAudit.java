package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StartAudit extends DataObject {
	public Date date;
	public String id;
	
	public List<CellsAuditItem> items = new ArrayList<CellsAuditItem>();
}
