package com.grsoft.dataobjects.impl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.LoadedOrdersRcvr;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrderWhItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.WhData;
import com.grsoft.napoleon.OrderLoadedDetail;
import com.grsoft.napoleon.PriceCountOrder;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;
import android.database.Cursor;

public class OrderImplEx extends OrderImpl {
	Map<String, Integer> qtys = null;
	
	@Override
	public String getDescription(Context context) {
		String desc = ((data.params & LoadedOrdersRcvr.FL_LOADED) != 0) ? "загружен" : 
				(data.podRemark.length() > 0) ? data.podRemark : 
				(isProceeded()) ?  "в обработке" : 
				(isExported()) ? "отправлен" : 
				""; 

		return (data.number.length() > 0) ? 
				data.number + "\n" + desc : 
				desc; 
	}
	
	@Override protected boolean checkPriceQty() { return false; }
	
	@Override
	public int getItemValue(Price item) {
		if(qtys == null) {
			qtys = new HashMap<String, Integer>();
			try {
				String stmt = "select sum(qty), id from whqty group by id";
				Cursor c = DataBaseManager.getDataBase().rawQuery(stmt, null);
				while(c.moveToNext()) {
					qtys.put(c.getString(1), c.getInt(0));
				}
				c.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		Integer val = qtys.get(item.id);
		return val == null ? 0 : val;
	}
	
	@Override
	public void editItem(long itemRowid, Context context) {
		PriceCountOrder.open(context, itemRowid, this);
	}
	
	@Override
	public long sum() {
		LoadedOrdersImpl li = new LoadedOrdersImpl();
		if(li.read("created", data.created))
			return li.getData().sum();

		return super.sum();
	}
	
	@Override
	public void open(Context context) {
		boolean haveDoc = false;
		if(data.number.length() == 0) {
			LoadedOrdersImpl li = new LoadedOrdersImpl();
			haveDoc = li.read("created", data.created);
		}
		if(!haveDoc)
			super.open(context);
		else
			OrderLoadedDetail.openDoc(context, this);
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
	}
	
	@Override
	protected void postCopyProcess(CreatableDocument<Order> copy) {
		super.postCopyProcess(copy);
		
		Order o = copy.getData();

		Calendar c = Calendar.getInstance();
		c.setTime(o.date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		
		if( c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY )
			c.add(Calendar.DAY_OF_MONTH, 1);
		
		o.date = c.getTime();

		for(OrderItem oi : o.items) {
			((OrderItemEx)oi).uid = UUID.randomUUID().toString().replace("-", "");
		}
	}
	
	@Override
	public boolean delete() {
		if( !isExported() && data.items != null && checkPriceQty() )
		{
			for(OrderItem oie : data.items) {
				HashMap<String, Integer> qty = new HashMap<String, Integer>();
				for(OrderWhItem owi : ((OrderItemEx)oie).whData) {
					qty.put(owi.id, owi.qty);
				}
				
				if( qty.size() > 0 ) {
					WhData.updateQty(oie.id, qty);
				}
			}
		}		
		return super.delete();
	}
}
