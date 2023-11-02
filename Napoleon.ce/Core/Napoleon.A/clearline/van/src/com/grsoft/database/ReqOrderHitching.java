package com.grsoft.database;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.database.Cursor;

public class ReqOrderHitching extends Hitching {
	
	public interface Handler {
		void onNewOrders();
	}
	
	Set<String> exists = new HashSet<String>();
	Handler handler;
	
	boolean haveNewOrders = false;
	
	public ReqOrderHitching() {
		super(OrderEx.class, "RequestOrders");
	}
	
	public void setHandler(Handler h) { this.handler = h; }

	@Override
	public void onStart() {
		super.onStart();
		
		OrderEx o = new OrderEx();
		Date checkDate = new Date(Util.getDate().getTime() - 3 * 24 * 3600 * Consts.ONE_SECOND);
		String sql = "select orderNumber from [" + o.getTableName() + "] where created >= " + Long.toString(checkDate.getTime());
		
		try {
			Cursor c = DataBaseManager.getDataBase().rawQuery(sql, null);
			while(c.moveToNext()) {
				exists.add(c.getString(0));
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		OrderEx dobj = (OrderEx) rawObject.createDataObject(dataObject);
		dobj.params |= ParamState.ofExported | ParamState.ofProceeded;
				
		dbProxy.insertRecord(dobj);
		
		if( !exists.contains(dobj.orderNumber) )
			haveNewOrders = true;
	}
	
		
	@Override
	public void onEnd() {
		super.onEnd();
		
		exists.clear();
		if(haveNewOrders && handler != null)
			handler.onNewOrders();
	}
	
}
