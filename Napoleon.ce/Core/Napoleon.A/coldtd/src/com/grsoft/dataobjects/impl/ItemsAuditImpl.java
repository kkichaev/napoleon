package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.ItemsAudit;
import com.grsoft.dataobjects.ItemsAuditItem;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.ItemsAuditDetail;
import com.grsoft.napoleon.documents.CreatableDocument;

public class ItemsAuditImpl extends CreatableDocument<ItemsAudit> {

	@Override
	public void open(Context context) {
		ItemsAuditDetail.open(context, this);
	}
	
	@Override
	public void postInit() {
		OrgImpl oi = new OrgImpl();
		OrgEx o = (OrgEx)oi.getData();
		o.id = data.id;
		oi.read();
		oi.close();
		
		
		for(MatrixItem mi : o.matrix) {
			ItemsAuditItem ii = new ItemsAuditItem();
			ii.id = mi.id;
			data.items.add(ii);
		}
	}
}
