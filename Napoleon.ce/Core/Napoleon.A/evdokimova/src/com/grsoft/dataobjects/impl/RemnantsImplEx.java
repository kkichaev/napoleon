package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.napoleon.documents.RemnantsDoc;

public class RemnantsImplEx extends RemnantsImpl {
	public void updateItem(String id, boolean exists) {
		RemnantItemEx item = (RemnantItemEx) findItem(id);
		if(exists) {
			if(item == null) {
				item = new RemnantItemEx();
				item.id = id;
				data.items.add(item);
			}
			item.exists = 1;
		} else {
			if(item != null) {
				data.items.remove(item);
			}
		}
		
		write();
		RemnantsDoc.instance().refreshDocSum(data.id);
	}
}
