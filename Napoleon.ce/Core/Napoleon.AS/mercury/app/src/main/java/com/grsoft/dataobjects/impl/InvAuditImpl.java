package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.dataobjects.InvAudit;
import com.grsoft.dataobjects.InvAuditItem;
import com.grsoft.napoleon.InventoryEdit;
import com.grsoft.napoleon.documents.CreatableDocument;


public class InvAuditImpl extends CreatableDocument<InvAudit> {

	@Override
	public void open(Context context) {
		InventoryEdit.open(context, data.created.getTime());
	}
	
	private boolean hasItem(String id){
		boolean result = false;
		
		for(InvAuditItem i : data.items)
			if(i.id.equals(id)){
				result = true;
				break;
			}
		
		return result;
	}

	public boolean addItem(String id, boolean isnew) {
		boolean result = !hasItem(id);
		
		if (result){
			InvAuditItem i = new InvAuditItem();
			i.id = id;
			i.isnew = isnew ? 1 : 0;
			
			data.items.add(i);
		}
		
		return result;
	}

	public void remItem(String id) {
		for(InvAuditItem i : data.items)
			if(i.id.equals(id)){
				data.items.remove(i);
				break;
			}
	}
	
	@Override
	public boolean isEmpty() {
		return data.items.size() == 0;
	}
}
