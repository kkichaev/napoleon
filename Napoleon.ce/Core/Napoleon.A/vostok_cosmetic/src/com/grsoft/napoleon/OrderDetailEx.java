package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.DatePeriod;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = findViewById(R.id.btnDiscount); 
		
		int maxDscDelta = 100;
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		c.key = "МаксимальнаяСкидка";
		if( ci.read() )
			maxDscDelta = Integer.parseInt(c.value);
		ci.close();
		
		if( CostStrategyEx.isNetUser() || maxDscDelta == 0 )
			v.setEnabled(false);
		else
			v.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) { OrderDiscounts.open(OrderDetailEx.this, doc); }
			});
	}
	
	@Override
	protected void setAdapter() {
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder value = new StringBuilder();
		if((cfg.getValue(value, "Usefg") 
				&& cfg.getData().value.equals("есть")) && ((OrderEx)doc.getData()).autoorder == 0)
			lvItems.setAdapter(new OrderItemsAdapterEx());
		else
			lvItems.setAdapter(new OrderItemsAdapter());
	}

	class OrderItemsAdapterEx extends OrderItemsAdapter
	{
		List<OrderItem> assortMtx = new ArrayList<OrderItem>();
		
		protected void setItems(List<OrderItem> items) {
			this.items = items;
			collectItems();
			notifyDataSetChanged();
		}
		
		private void collectItems() {
			HashSet<String> pidset = new HashSet<String>();
			assortMtx.clear();
			
			for(OrderItem i: items)
				if(!pidset.contains(i.id))
					pidset.add(i.id);
			
			HashMap<String, List<OrderItem>> map = new HashMap<String, List<OrderItem>>(); 
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			Date end = calendar.getTime();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.add(Calendar.MONTH, -AssortmentMatrixAdapter.PERIOD_IN_MONTH);
			Date begin = calendar.getTime();
			DatePeriod dp = new DatePeriod(begin, end);
			dp.periodType = DatePeriod.CREATED;
			
			DocList dl = OrderDoc.instance().docList(doc.getId(), null, dp);
			
			for(int i = 0; i < dl.getCount(); i++){
				Document<?> d = dl.get(i);
				if(d instanceof OrderImpl){
					Order o = ((OrderImpl)d).getData();
					
					if(o.items != null && o.items.size() > 0)
						for(OrderItem oi : o.items)
						{
							List<OrderItem> list = null;
							if (map.containsKey(oi.id))
								list = map.get(oi.id);
							else
							{
								list = new ArrayList<OrderItem>();
								map.put(oi.id, list);
							}
							
							list.add(oi);
						}
				} 
			}
			
			Iterator<Entry<String, List<OrderItem>>> it = map.entrySet().iterator();
			CostStrategy strategy = CostStrategy.getInstance(OrderImpl.class);
			
			while (it.hasNext()) {
				Entry<String, List<OrderItem>> entry = it.next();
				
				if(pidset.contains(entry.getKey()))
					continue;
				
				int qty = 0;
				int cnt = 0;
				
				for(OrderItem i : entry.getValue()) {
					qty += i.qty;
					cnt++;
				}
				
				OrderItem newItem = new OrderItem();
				newItem.qty = cnt == 0 ? 0 : qty / cnt;
				newItem.id = entry.getKey();
				
				PriceImpl priceImpl = new PriceImpl();
				priceImpl.getData().id = newItem.id;
				
				if(priceImpl.read()){
					newItem.cost = strategy.getItemCost(priceImpl.getData(), doc);
					assortMtx.add(newItem);
				}
					
				priceImpl.close();
		    }
		}
		
		@Override
		public int getCount() {
			return super.getCount() + assortMtx.size();
		}
		
		@Override
		public long getItemId(int pos) {
			if(pos<super.getCount())
				return 0;
			else
				return 1;
		}
		
		@Override
		protected int getItemColor(int pos) {
			if(getItemId(pos) == 0)
				return Color.BLACK;
			else
				return Color.RED;
		}
		
		@Override
		public Object getItem(int pos) {
			if(pos < items.size())
				return super.getItem(pos);
			else
				return assortMtx.get(pos - items.size());
		}
	}
}
