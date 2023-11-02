package com.grsoft.ads.database;

import android.database.sqlite.SQLiteStatement;

import com.grsoft.ads.dataobjects.Order;
import com.grsoft.ads.dataobjects.OrderDel;
import com.grsoft.ads.dataobjects.impl.OrderImpl;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class OrderDelHitching extends Hitching {
	
	private SQLiteStatement delete;
	
	public OrderDelHitching() {
		super(DbObject.getDataType(OrderDel.class), "OrderDel");
		
		Class<? extends DataObject> orderType = DbObject.getDataType(Order.class); 
		DbWriter.checkDBTable(orderType);
		delete = DataBaseManager.getDataBase().compileStatement("DELETE FROM [" + 
				DataObjectInfo.getInstance().getTableName(orderType) + "] WHERE created=?");
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		OrderDel orderDel = (OrderDel)rawObject.createDataObject(
				DbObject.getDataType(OrderDel.class));
		
		OrderImpl orderImpl = new OrderImpl();
		orderImpl.getData().created = orderDel.created;
		
		if (orderImpl.read() && 
				orderImpl.getData().params == 0 &&
				delete != null){
				delete.bindLong(1, orderDel.created.getTime());
				delete.execute();
		}
			
		orderImpl.close();
			
	}

}
