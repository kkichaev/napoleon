package com.grsoft.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.OrderReserv;
import com.grsoft.dataobjects.ParamStateEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class OrderReservHitching extends Hitching {
	private SQLiteStatement statement;
	private SQLiteDatabase database;

	public OrderReservHitching() {
		super(DbObject.getDataType(Delivery.class), "DeliveryDay");
		database = DataBaseManager.getDataBase();
	}

	@Override
	public void onStart() {
		super.onStart();
		try {
			statement = database.compileStatement("UPDATE orders SET orderNumber=?, params=params | ? WHERE created=?");
		} catch (Exception e) {
		}
	}

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {

		OrderReserv delivery = (OrderReserv) rawObject.createDataObject(OrderReserv.class);
		dbProxy.insertRecord(delivery);

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

	@Override
	public void onEnd() {
		super.onEnd();
		if (statement != null)
			statement.close();
	}
}
