package com.grsoft.napoleon;

import java.util.List;
import com.grsoft.database.Hitching;
import com.grsoft.database.WorkTimeExport;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadService;
import com.grsoft.network.ReadServiceBase;
import com.grsoft.network.WriteService;
import com.grsoft.network.WriteServiceBase;

public class RWServiceFactoryNapoleon extends RWServiceFactory {
	public ReadServiceBase createReadService(List<Hitching> hitchings) {
		return new ReadService(hitchings);
	}

	public WriteServiceBase createWriteService(List<? extends ObjectListener> objectsToSend) {
		return createWriteService(objectsToSend, false);
	}

	@SuppressWarnings("unchecked")
	public WriteServiceBase createWriteService(List<? extends ObjectListener> objectsToSend, boolean rcvRemnants) {
		
		if(Features.START_STOP && objectsToSend != null){
			((List<ObjectExportListener>)objectsToSend).add(new WorkTimeExport());
		}
		
		return new WriteService(objectsToSend, rcvRemnants);
	}
}
