package com.grsoft.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.network.exception.RuntimeException;

public class DeliveryHitching extends RcvNewHitching {

	protected SQLiteStatement statement;
	List<Date> created = new ArrayList<Date>(); 
	
	public DeliveryHitching() {
		super(DbObject.getDataType(Delivery.class), "Delivery");
		clearNumbers();
	}

	public DeliveryHitching(boolean keepNumbers) {
		super(DbObject.getDataType(Delivery.class), "Delivery");
		if(!keepNumbers)
			clearNumbers();
	}
	
	protected void clearNumbers() {
		try{
			SQLiteDatabase database = DataBaseManager.getDataBase();
			database.execSQL("UPDATE [orders] SET number=''");
		}catch(Exception e){}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		try{
			SQLiteDatabase database  = DataBaseManager.getDataBase();
			statement = database.compileStatement("UPDATE orders SET number= (number || ?) WHERE created=?");
		}catch(Exception e)
		{
		}
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
