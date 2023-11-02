package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Equip;
import com.grsoft.dataobjects.InvEqu;
import com.grsoft.dataobjects.InvEquItem;
import com.grsoft.napoleon.InvEquEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;

public class InvEquImpl extends CreatableDocument<InvEqu> {

	@Override
	public void open(Context context) {
		InvEquEdit.open(context, getRowid());
	}
	
	@Override
	public void postInit() {
		super.postInit();
		
		DataTraveler.travel(Equip.class, new DataTraveler.Travel<Equip>(true) {

			@Override
			public boolean travel(DataTraveler<Equip> item) {
				InvEquItem i = new InvEquItem();
				i.number = item.data.number;
				i.name = item.data.name;
				i.id = item.data.id;
				i.barcode = item.data.barcode;
				
				data.items.add(i);
				return true;
			}
		}, String.format("ido='%s'", getId()));

	}
	
	public InvEquItem getItemByBarcode(String barcode) {
		InvEquItem res = null;
		
		for(InvEquItem i : data.items)
			if (i.barcode.equals(barcode)) {
				res = i;
				break;
			}
				
		return res;
	}
	
	public InvEquItem findItem(String id) {
		for (InvEquItem i : data.items)
			if (i.id.equals(id))
				return i;
			
		return null;
	}
}
