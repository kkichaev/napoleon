package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Rfrg;
import com.grsoft.dataobjects.RfrgAudit;
import com.grsoft.dataobjects.RfrgAuditItem;
import com.grsoft.napoleon.RfrgAuditDetail;
import com.grsoft.napoleon.documents.CreatableDocument;

public class RfrgAuditImpl extends CreatableDocument<RfrgAudit> {

	@Override
	public void open(Context context) {
		RfrgAuditDetail.open(context, this);
	}

	@Override
	public void postInit() {
		DataTraveler.travel(Rfrg.class, new DataTraveler.Travel<Rfrg>() {

			@Override
			public boolean travel(DataTraveler<Rfrg> item) {
				RfrgAuditItem i = new RfrgAuditItem();
				i.doc_id = item.data.id;
				i.model = item.data.model;
				i.descr = item.data.type;
				data.items.add(i);
				return true;
			}
		}, "ido='" + data.id + "'");
	}
}
