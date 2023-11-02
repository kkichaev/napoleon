package com.grsoft.network;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DayDeliveryHitchingTarpan;
import com.grsoft.database.DlvQueryHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.PODHitching;
import com.grsoft.database.PODHitchingEx;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Update;
import com.grsoft.napoleon.RWServiceFactoryNapoleon;

public class RWServiceFactoryEx extends RWServiceFactoryNapoleon {
	RcvNewHitching updateHitch = new RcvNewHitching(Update.class, "Update"); 
	DayDeliveryHitchingTarpan dayDlvHitch = new DayDeliveryHitchingTarpan();
	
	@Override
	public ReadServiceBase createReadService(List<Hitching> hitchings) {
		hitchings.add(updateHitch);
		hitchings.add(dayDlvHitch);
		ReadService result = new ReadService(hitchings){
			@Override
			protected PODHitching createPODHitching() {
				return new PODHitchingEx();
			}
		};
		return result;
	}
	
	@Override
	public WriteServiceBase createWriteService(
			List<? extends ObjectListener> objectsToSend, boolean rcvRemnants) {
		ArrayList<ObjectListener> list = new ArrayList<ObjectListener>();
		list.addAll(objectsToSend);
		list.add(new DlvQueryHitching());
		WriteServiceBase result = new WriteService(list, rcvRemnants){
			@Override
			protected PODHitching createPODHitching() {
				return new PODHitchingEx();
			}
		};
		
		result.addRecieveHitch(updateHitch);
		return result;
	}
	
	@Override
	public WriteServiceBase createWriteService(
			List<? extends ObjectListener> objectsToSend) {
		return createWriteService(objectsToSend, false);
	}
}