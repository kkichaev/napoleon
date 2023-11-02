package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="GoodsAudit", keyFields="created")
public class GoodsAudit extends CreateDocDataObject {
	public List<GoodsAuditItem> items = new ArrayList<GoodsAuditItem>();
}
