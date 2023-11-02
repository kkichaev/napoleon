package com.grsoft.dataobjects;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DeliveryHitching;
import com.grsoft.napoleon.UpdateDB;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected DeliveryHitching getDeliveryHitching() {
		return new DeliveryHitching(){
			@Override
			public void onStart() {
				super.onStart();
				try {
					statement = DataBaseManager.getDataBase()
							.compileStatement("UPDATE orders SET number=?, params=params | ? WHERE created=?");
				} catch (Exception e) {
				}
			}

			@Override
			public void onRead(RawObject rawObject) throws RuntimeException {
				super.onRead(rawObject);
				Delivery delivery = (Delivery) rawObject
						.createDataObject(Delivery.class);

				if (statement != null && delivery.created != null) {
					statement.clearBindings();
					statement.bindString(1, delivery.number);
					statement.bindLong(2, ParamStateEx.ofConfirm);
					statement.bindLong(3, delivery.created.getTime());

					try {
						statement.execute();
					} catch (Exception e) {
					}
				}
			}
		};
	}
}
