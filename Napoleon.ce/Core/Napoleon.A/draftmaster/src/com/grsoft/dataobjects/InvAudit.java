package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.grsoft.database.TableInfo;

@TableInfo(name="invaudit", keyFields="created")
public class InvAudit extends CreateDocDataObject {
	public Date penult;
	public Date last;
	
	public List<InvAuditItem> items = new ArrayList<InvAuditItem>();
}
