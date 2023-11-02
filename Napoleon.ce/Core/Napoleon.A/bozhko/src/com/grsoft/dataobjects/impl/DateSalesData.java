package com.grsoft.dataobjects.impl;

import java.util.Date;
import java.util.HashMap;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.DatePeriod;

public class DateSalesData extends HashMap<String, SalesData> {
	private static final long serialVersionUID = 1L;

	void load(Date start, Date end) {
		DatePeriod dp = new DatePeriod(start, end);
		dp.periodType = DatePeriod.CREATED;
		
		DocList dl = OrderDoc.instance().docList(null, null, dp);
		for(Document<?> doc : dl) {
			OrderImpl ord = (OrderImpl)doc;
			for(OrderItem oi : ord.getData().items) {
				SalesData sd = get(oi.id);
				if( sd == null ) {
					sd = new SalesData();
					put(oi.id, sd);
				}
				sd.put(ord.getData().created, oi.qty);
			}
		}
		dl.close();
	}
	
	public int getSales(String id, Date start, Date end) {
		int qty = 0;
		
		SalesData sd = get(id);
		if( sd != null )
			qty = sd.getSales(start, end);
		
		return qty;
	}
}