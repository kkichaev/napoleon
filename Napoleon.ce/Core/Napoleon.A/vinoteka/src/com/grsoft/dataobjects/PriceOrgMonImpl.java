package com.grsoft.dataobjects;

import android.content.Context;

import com.grsoft.napoleon.PriceOrgMonEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.PriceOrgMonDoc;

public class PriceOrgMonImpl extends CreatableDocument<PriceOrgMon> {

	@Override public void open(Context context) { PriceOrgMonEdit.open(context, this); }

	@Override
	public void postInit() {
		DataTraveler.travel(MonItem.class, new DataTraveler.Travel<MonItem>() {

			@Override
			public boolean travel(DataTraveler<MonItem> item) {
				PriceOrgMonItem i = new PriceOrgMonItem();
				i.id = item.data.id;
				i.cost = 0;
				data.items.add(i);
				return true;
			}
		}, "");
	}
	
	@Override
	public long write() {
		long ret = super.write();
		PriceOrgMonDoc.instance().refreshDocSum(getId());
		return ret;
	}
}
