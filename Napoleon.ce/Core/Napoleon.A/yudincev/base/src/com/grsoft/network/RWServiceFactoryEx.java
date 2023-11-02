package com.grsoft.network;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DayDeliveryHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.PODHitching;
import com.grsoft.napoleon.RWServiceFactoryNapoleon;

public class RWServiceFactoryEx extends RWServiceFactoryNapoleon {
	DayDeliveryHitching dayDlvHitch = new DayDeliveryHitching();
	
	@Override
	public ReadServiceBase createReadService(List<Hitching> hitchings) {
		hitchings.add(dayDlvHitch);
		ReadService result = new ReadService(hitchings){
			@Override
			protected PODHitching createPODHitching() {
				return PODHitching.instance();
			}
		};
		return result;
	}
	
	@Override
	public WriteServiceBase createWriteService(
			List<? extends ObjectListener> objectsToSend, boolean rcvRemnants) {
		ArrayList<ObjectListener> list = new ArrayList<ObjectListener>();
		list.addAll(objectsToSend);
		WriteServiceBase result = new WriteService(list, rcvRemnants){
			@Override
			protected PODHitching createPODHitching() {
				return PODHitching.instance();
			}
		};
		
		result.recieveHitch.add(dayDlvHitch);
		return result;
	}
	
	@Override
	public WriteServiceBase createWriteService(
			List<? extends ObjectListener> objectsToSend) {
		return createWriteService(objectsToSend, false);
	}
}