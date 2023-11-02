package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderToDel;
import com.grsoft.dataobjects.impl.OrderToDelImpl;
import com.grsoft.network.ObjectExportListener;

public class OrderToDelHitching extends Hitching implements
		ObjectExportListener {

	List<Long> list;
	public OrderToDelHitching() {
		super(OrderToDel.class, "OrderDel");
		
		list = new ArrayList<Long>();
		list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(OrderToDel.class), "", "");
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public DataObject get(int i) {
		OrderToDelImpl impl = new OrderToDelImpl();
		impl.read(list.get(i));
		impl.close();
		
		return impl.getData();
	}
	
	@Override
	public void onEnd() {
		SQLiteDatabase db = DataBaseManager.getDataBase();
		db.delete(DataObjectInfo.getInstance().getTableName(OrderToDel.class), 
				null, null);
	}

}
