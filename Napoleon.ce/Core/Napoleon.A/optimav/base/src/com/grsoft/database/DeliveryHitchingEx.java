package com.grsoft.database;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.IDelivery;

public class DeliveryHitchingEx extends DeliveryHitching {
	public void onRead(com.grsoft.network.RawObject rawObject) 
			throws com.grsoft.network.exception.RuntimeException {
		Delivery delivery = (Delivery) rawObject.createDataObject(dataObject);
		((IDelivery)delivery).setSumD(delivery.sumD);
		
		dbProxy.insertRecord(delivery);
		
		
		
		if (statement != null && delivery.created != null){
			statement.clearBindings();
			statement.bindString(1, delivery.number);
			statement.bindLong(2, delivery.created.getTime());
			
			try{
				statement.execute();
			}catch(Exception e){}
		}
	};
}
