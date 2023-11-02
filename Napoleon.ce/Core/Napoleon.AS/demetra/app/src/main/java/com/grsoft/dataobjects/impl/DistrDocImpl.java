package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DistrDoc;
import com.grsoft.dataobjects.DistrItem;
import com.grsoft.dataobjects.DistribGroup;
import com.grsoft.napoleon.DistrEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

public class DistrDocImpl extends CreatableDocument<DistrDoc> {

	@Override
	public void open(Context context) {
		DistrEdit.open(context, this);
	}

	@Override
	public void postInit() {
		DataTraveler.travel(DistribGroup.class, new DataTraveler.Travel<DistribGroup>(){

			@Override
			public boolean travel(DataTraveler<DistribGroup> item) {
				DistrItem di = new DistrItem();
				di.id = item.data.id;
				di.exists = 0;
				data.items.add(di);
				return true;
			}
			
		}, "", "pos");
	}
}
