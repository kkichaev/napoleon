package com.grsoft.network;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.DayDeliveryHitching;
import com.grsoft.database.DayDeliveryHitchingEx;
import com.grsoft.database.Hitching;
import com.grsoft.database.PODHitching;
import com.grsoft.database.PODHitchingEx;
import com.grsoft.napoleon.RWServiceFactoryNapoleon;

public class RWServiceFactoryEx extends RWServiceFactoryNapoleon {
	DayDeliveryHitchingEx dayDlvHitch = new DayDeliveryHitchingEx();
	
	@Override
	public ReadServiceBase createReadService(List<Hitching> hitchings) {
		hitchings.add(dayDlvHitch);
		
		ReadService result = new ReadService(hitchings){
			@Override protected PODHitching createPODHitching() { return new PODHitchingEx(); }
		};
		
		return result;
	}
	
	@Override
	public WriteServiceBase createWriteService(List<? extends ObjectListener> objectsToSend, boolean rcvRemnants) {


		ArrayList<ObjectListener> list = new ArrayList<ObjectListener>();

		if (objectsToSend != null)
			list.addAll(objectsToSend);

		WriteServiceBase result = new WriteService(list, rcvRemnants){
			@Override protected PODHitching createPODHitching() { return new PODHitchingEx(); }
		};
		
		return result;
	}
	
	@Override
	public WriteServiceBase createWriteService(List<? extends ObjectListener> objectsToSend) {
		return createWriteService(objectsToSend, false);
	}
}