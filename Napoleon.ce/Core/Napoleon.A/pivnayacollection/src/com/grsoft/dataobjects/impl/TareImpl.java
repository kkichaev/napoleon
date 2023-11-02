package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgInv;
import com.grsoft.dataobjects.Tare;
import com.grsoft.dataobjects.TareItem;
import com.grsoft.napoleon.TareEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;
import android.widget.Toast;

public class TareImpl extends CreatableDocument<Tare> {

	@Override
	public void open(Context context) {
		
		if(data.items.size() > 0)
			TareEdit.open(context, this);
		else{
			delete();
			Toast.makeText(context, "Не выгружена тара для контрагента", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void postInit() {
		DataTraveler.travel(OrgInv.class, new DataTraveler.Travel<OrgInv>() {

			@Override
			public boolean travel(DataTraveler<OrgInv> item) {
				TareItem i = new TareItem();
				i.id = item.data.id_i;
				i.qty = item.data.qty;
				data.items.add(i);
				return true;
				
			}}, "id='"+data.id+"' and tare=1");
	}
}
