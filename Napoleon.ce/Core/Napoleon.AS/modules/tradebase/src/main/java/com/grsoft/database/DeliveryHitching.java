package com.grsoft.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.network.exception.RuntimeException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeliveryHitching extends RcvNewHitching {

	protected SQLiteStatement statement;
	protected SQLiteDatabase database;
	List<Date> created = new ArrayList<Date>();

	public DeliveryHitching() {
		this("Delivery");
	}

	public DeliveryHitching(String objName) {
		super(DbObject.getDataType(Delivery.class), objName);
		database = DataBaseManager.getDataBase();
		try{
			database.execSQL("UPDATE [orders] SET number=''");
		}catch(Exception e){}
	}


	@Override
	public void onStart() {
		super.onStart();
		try{
			statement = database.compileStatement("UPDATE orders SET number=(number || ?) WHERE created=?");
		}catch(Exception e){}
	}
	
	@Override
	protected void postRead(DataObject dobj) {
		Delivery delivery = (Delivery) dobj;
		
		if (statement != null && delivery.created != null){
			statement.clearBindings();
			String number = delivery.number;

			if(created.contains(delivery.created)) number = "," + number;
			else created.add(delivery.created);

			statement.bindString(1, number);
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
