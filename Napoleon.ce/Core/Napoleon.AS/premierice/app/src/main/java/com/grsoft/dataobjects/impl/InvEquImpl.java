package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.AuditEquip;
import com.grsoft.dataobjects.AuditEquipItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Rfrgr;
import com.grsoft.napoleon.AuditEquipEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;

public class InvEquImpl extends CreatableDocument<AuditEquip> {

	@Override
	public void open(Context context) {
		AuditEquipEdit.open(context, getRowid());
	}
	
	@Override
	public void postInit() {
		super.postInit();

		OrgImpl oi = new OrgImpl();
		OrgEx o = (OrgEx) oi.getData();
		o.id = data.id;
		oi.read();
		oi.close();

		for(Rfrgr r : o.rfrgr) {
			AuditEquipItem i = new AuditEquipItem();
			i.id = r.id;

			data.items.add(i);
		}
	}

	@Override
	public boolean isEmpty() {
		return data.items.isEmpty();
	}
}
