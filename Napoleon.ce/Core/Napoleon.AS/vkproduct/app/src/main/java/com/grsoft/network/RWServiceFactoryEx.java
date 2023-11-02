package com.grsoft.network;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.napoleon.RWServiceFactoryNapoleon;

public class RWServiceFactoryEx extends RWServiceFactoryNapoleon {
	@Override
	public WriteService createWriteService(List<? extends ObjectListener> objectsToSend, boolean rcvRemnants) {
		return new WriteServiceEx(objectsToSend, rcvRemnants);
	}
	
	@Override
	public ReadService createReadService(List<Hitching> hitchings) {
		return new ReadServiceEx(hitchings);
	}
}
