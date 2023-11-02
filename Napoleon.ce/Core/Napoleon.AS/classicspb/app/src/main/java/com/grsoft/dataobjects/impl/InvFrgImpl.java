package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Fridge;
import com.grsoft.dataobjects.InvFrg;
import com.grsoft.dataobjects.InvFrgItem;
import com.grsoft.napoleon.InvFrgEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;

public class InvFrgImpl extends CreatableDocument<InvFrg> {

	@Override
	public void open(Context context) {
		InvFrgEdit.open(context, getRowid());
	}
	
	@Override
	public void postInit() {
		super.postInit();
		
		DataTraveler.travel(Fridge.class, new DataTraveler.Travel<Fridge>(true) {

			@Override
			public boolean travel(DataTraveler<Fridge> item) {
				InvFrgItem i = new InvFrgItem();
				i.number = item.data.number;
				i.name = item.data.name;
				i.id = item.data.id;
				
				data.items.add(i);
				return true;
			}
		}, String.format("ido='%s'", getId()));

	}
	
	public InvFrgItem getItem(String id) {
		InvFrgItem res = null;
		
		for(InvFrgItem i : data.items)
			if (i.id.equals(id)) {
				res = i;
				break;
			}
				
		return res;
	}
}
