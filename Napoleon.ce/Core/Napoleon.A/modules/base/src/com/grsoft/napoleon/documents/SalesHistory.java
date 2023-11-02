package com.grsoft.napoleon.documents;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.Features;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

@SuppressWarnings("serial")
public class SalesHistory  extends HashMap<Long, Integer>
{
	@Override
	public Integer put(Long key, Integer value) {
		if (containsKey(key))
			value += get(key);
		
		return super.put(key, value);
	}
	
	void putData(Date date, int qty, int weight) {
		Date maskDate = new Date(date.getYear(), date.getMonth(), date.getDate());

		if( weight != 0 ) {
			qty = (int)(((long)qty * weight + Consts.WEIGHT_SCALE/2) / Consts.WEIGHT_SCALE);
			qty -= (qty % Consts.WEIGHT_SCALE); // округлим 
		}
		put(maskDate.getTime(), qty);
	}
	
	protected void putItem(Document<?> doc, OrderItem item, int weight) {
		putData(doc.getDate(), item.qty, weight);
	}

	protected void putItem(Document<?> doc, DeliveryItem item, int weight) {
		putData(doc.getDate(), item.qty, weight);
	}
	
	public void create(String orgId, String priceId, boolean fromOrders) {
		int weight = 0;
		if( Features.SHOW_WEIGHT_IN_HISTORY ) {
			PriceImpl pi = new PriceImpl();
			Price p = pi.getData();
			p.id = priceId;
			
			if( pi.read() )
				weight = p.weight;
			pi.close();
		}
		
		DocList list = (fromOrders) ? OrderDoc.instance().docList(orgId) : DeliveryDoc.instance().docList(orgId);
		for( Document<?> doc : list ) {
			DataObject od = doc.getData();
			if(od instanceof Order) {
				Order o = (Order)od;
				for (OrderItem orderItem: o.items) {
					if (orderItem.id.equals(priceId))
						putItem(doc, orderItem, weight);
				}				
			} else if(od instanceof Delivery) {
				Delivery d = (Delivery)od;
				for(DeliveryItem di : d.items) {
					if( di.id.equals(priceId))
						putItem(doc, di, weight);
				}
			}
		}
		list.close();
	}


	static public String[] getHistory(String orgId, String priceId, boolean fromOrders) {
		String items[] = null;
		
		try {
			SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM", Locale.getDefault());

			SalesHistory history = new SalesHistory();
			history.create(orgId, priceId, fromOrders) ;

			ArrayList<Entry<Long, Integer>> saleHistory = new ArrayList<Entry<Long,Integer>>();
			saleHistory.addAll(history.entrySet());
			
			Collections.sort(saleHistory, new CmpHistory());
			
			int ctr = 0;
			items = new String[saleHistory.size() * 2];
			for (Entry<Long, Integer> entry: saleHistory) {
				items[ctr++] = simpleDateFormat.format(new Date(entry.getKey()));
				items[ctr++] = Util.IntToScaleStr(entry.getValue(), Consts.QTY_SCALE);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return items;
	}
}

