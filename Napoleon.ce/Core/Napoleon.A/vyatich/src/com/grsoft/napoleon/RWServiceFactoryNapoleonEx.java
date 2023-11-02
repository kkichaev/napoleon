package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.ReadServiceBase;
import com.grsoft.network.ReadServiceEx;
import com.grsoft.network.WriteServiceBase;
import com.grsoft.network.WriteServiceEx;

public class RWServiceFactoryNapoleonEx extends RWServiceFactoryNapoleon {
	@Override
	public WriteServiceBase createWriteService(List<? extends ObjectListener> objectsToSend, boolean rcvRemnants) {
		return new WriteServiceEx(objectsToSend, rcvRemnants);
	}
	
	@Override
	public ReadServiceBase createReadService(List<Hitching> hitchings) {
		return new ReadServiceEx(hitchings);
	}
}
