package com.grsoft.network;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.WorkTimeExport;
import com.grsoft.napoleon.RWServiceFactoryNapoleon;

public class RWServiceFactoryEx extends RWServiceFactoryNapoleon {
	@Override
	public WriteServiceBase createWriteService(
			List<? extends ObjectListener> objectsToSend, boolean rcvRemnants) {
		ArrayList<ObjectListener> list = new ArrayList<ObjectListener>();
		
		list.addAll(objectsToSend);
		list.add(new WorkTimeExport());
		
		return super.createWriteService(list, rcvRemnants);
	}
}