package com.grsoft.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class DayDeliveryHitchingEx extends Hitching {
	private SQLiteStatement statement;
	private SQLiteDatabase database; 
	
	public DayDeliveryHitchingEx() {
		super(DbObject.getDataType(Delivery.class), "DeliveryDay");
		database = DataBaseManager.getDataBase();
		try{
			database.execSQL("UPDATE [orders] SET number=''");
		}catch(Exception e){}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		try{
			statement = database.compileStatement("UPDATE orders SET number=? WHERE created=?");
		}catch(Exception e){}
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		super.onRead(rawObject);
		Delivery delivery = (Delivery) rawObject.createDataObject(Delivery.class);
		
		if (statement != null && delivery.created != null){
			statement.clearBindings();
			statement.bindString(1, delivery.number);
			statement.bindLong(2, delivery.created.getTime());
			
			try{
				statement.execute();
			}catch(Exception e){}
		}
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		if (statement != null)
			statement.close();
		
		if(Features.DELIVERY_REPLACE_ORDER_SUM) {
			try {
				OrderDoc.instance().refreshDocSum();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}
}
