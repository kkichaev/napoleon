package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="commonAudit", keyFields="created")
public class CommonAudit extends CreateDocDataObject {
	public List<CommonAuditItem> items = new ArrayList<CommonAuditItem>();
	public List<AnswerId> answer = new ArrayList<AnswerId>();
}
