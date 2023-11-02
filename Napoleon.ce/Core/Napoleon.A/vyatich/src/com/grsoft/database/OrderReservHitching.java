package com.grsoft.database;

import java.util.ArrayList;

import android.database.sqlite.SQLiteStatement;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderReserv;
import com.grsoft.dataobjects.OrderReservItem;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Consts;

public class OrderReservHitching extends Hitching {
	
	Delivery dlv;
	DbWriter writer;
	protected SQLiteStatement statement;
	
	public OrderReservHitching() {
		super(OrderReserv.class, "OrderReserv");
		writer = new DbWriter();
		dlv = new Delivery();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		try{
			String table = DataObjectInfo.getInstance().getTableName(Order.class);
			statement = DataBaseManager.getDataBase().compileStatement("UPDATE [" + table + "] SET number=? WHERE created=?");
		}catch(Exception e){}
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		writer.close();
		
		if (statement != null)
			statement.close();
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		OrderReserv dobj = (OrderReserv) rawObject.createDataObject(dataObject);
		
		dlv.number = dobj.number;
		dlv.date = dobj.date;
		dlv.id = dobj.id;
		dlv.items = new ArrayList<DeliveryItem>();
		
		for(OrderReservItem i : dobj.items) {
			DeliveryItem di = new DeliveryItem();
			di.id = i.id;
			di.qty = i.qty;
			di.sum = (int)((long)(i.cost * i.qty) / Consts.QTY_SCALE);
			dlv.items.add(di);
		}
		
		if (statement != null ){
			statement.clearBindings();
			statement.bindString(1, dobj.number);
			statement.bindLong(2, dobj.created.getTime());
			
			try{
				statement.execute();
			}catch(Exception e){}
		}
		
		writer.insertRecord(dlv);
	}
}
