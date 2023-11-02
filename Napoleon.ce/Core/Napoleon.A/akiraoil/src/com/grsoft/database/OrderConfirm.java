package com.grsoft.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteStatement;

public class OrderConfirm extends HitchOnSelect {
	int daysBefore;
	protected SQLiteStatement statement;

	public OrderConfirm() {
		super(DeliveryEx.class, "OrderConfirm");
		daysBefore = 3;
	}

	public OrderConfirm(int daysBefore) {
		super(DeliveryEx.class, "OrderConfirm");
		this.daysBefore = daysBefore;
	}

	@Override
	public void onStart() {
		super.onStart();
		try{
			statement = DataBaseManager.getDataBase().compileStatement("UPDATE orders SET number=?, orderNumber=? WHERE orderTag=?");
		}catch(Exception e){}
	}
	
	@SuppressLint("SimpleDateFormat")
	@Override
	protected String getCondition() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, -daysBefore);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(c.getTime());
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		DataObject dobj = rawObject.createDataObject(dataObject);
		DeliveryEx de = (DeliveryEx) dobj;
		dbProxy.insertRecord(dobj);
		if (statement != null && de.orderTag.length() > 0){
			statement.clearBindings();
			statement.bindString(1, de.number);
			statement.bindString(2, de.orderNumber);
			statement.bindString(3, de.orderTag);
			
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
	}	
}
