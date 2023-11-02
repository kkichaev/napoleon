package com.grsoft.database;

import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.DbObject;

public class DayDeliveryHitching extends Hitching {
	private SQLiteStatement statement;
	Map<Date, String> created = new HashMap<Date, String>(); 

	public DayDeliveryHitching() {
		super(DbObject.getDataType(Delivery.class), "DeliveryDay");
	}

	@Override
	public void onStart() {
		super.onStart();
		try {
			statement = DataBaseManager.getDataBase().compileStatement("UPDATE orders SET number= ?, params=params | ? WHERE created=?");
		} catch (Exception e) {
		}
	}

	@Override
	protected void postRead(DataObject dobj) {
		Delivery delivery = (Delivery)dobj;
		
		if (statement != null && delivery.created != null) {
			statement.clearBindings();
			String s = created.get(delivery.created);
			if(s != null) s = s + "," + delivery.number;
			else s = delivery.number;
			created.put(delivery.created, s);
			statement.bindString(1, s);
			statement.bindLong(2, ParamState.ofProceeded);
			statement.bindLong(3, delivery.created.getTime());

			try {
				statement.execute();
			} catch (Exception e) {
			}
		}
		super.postRead(dobj);
	}

	@Override
	public void onEnd() {
		super.onEnd();
		if (statement != null)
			statement.close();

//		((DebtDocEx)DebtDoc.instance()).setNeedRefresh(true);
	}
}
