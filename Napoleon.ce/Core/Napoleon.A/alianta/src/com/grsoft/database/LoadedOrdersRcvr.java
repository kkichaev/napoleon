package com.grsoft.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.grsoft.dataobjects.LoadedOrderItem;
import com.grsoft.dataobjects.LoadedOrders;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import android.database.sqlite.SQLiteStatement;

public class LoadedOrdersRcvr extends HitchOnSelect {
	
	public static final int FL_LOADED = 0x1000;
	
	Map<Date, LoadedOrders> data = new HashMap<Date, LoadedOrders>();
	
	Date requestDate = null;
	
	public LoadedOrdersRcvr() {
		super(LoadedOrders.class, "LoadedOrders");
	}
	
	public LoadedOrdersRcvr(Date startDate) {
		super(LoadedOrders.class, "LoadedOrders");
		requestDate = startDate;
	}
	
	@Override
	protected String getCondition() {
		Date startDate = null;
		if(requestDate == null) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_MONTH, -3);
			startDate = c.getTime();
		} else {
			startDate = requestDate;
		}
		SimpleDateFormat sdf =  new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
		return String.format(" \"userid\" = '$CURRENT_USERID' and \"created\" >= ToDate('%s 00:00:00')", sdf.format(startDate));		
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		LoadedOrders doc = new LoadedOrders();
		rawObject.setDataObject(doc);
		
		LoadedOrders dest = data.get(doc.created);
		if(dest == null) {
			dest = doc;
			data.put(doc.created, doc);
		}
		
		LoadedOrderItem item = new LoadedOrderItem();
		rawObject.setDataObject(item);
		dest.items.add(item);
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
	
		if(data.size() > 0) {
			DbWriter.checkDBTable(OrderEx.class);
			
			String sql = "UPDATE " + (new Order()).getTableName() + " SET params = (params | ?), podRemark = ? WHERE created = ?";
			SQLiteStatement stmt = DataBaseManager.getDataBase().compileStatement(sql);
			DbWriter wr = new DbWriter();
			for(LoadedOrders doc : data.values()) {
				if(doc.items.size() > 0)
					wr.insertRecord(doc);
				stmt.clearBindings();
				stmt.bindLong(1, ParamState.ofProceeded | ParamState.ofExported | FL_LOADED);
				stmt.bindString(2, "Загружен");
				stmt.bindLong(3, doc.created.getTime());
				
				stmt.execute();
			}
			wr.close();
			stmt.close();
		
			try {
				OrderDoc.instance().refreshDocSum();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		data.clear();
	}
}
