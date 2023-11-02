package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OffTakeHistory;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;


public class OffTakeHistoryEx extends OffTakeHistory {
	private String priceid = "";  
	
	public OffTakeHistoryEx(String orgId, String priceid, boolean fromOrders) {
		super(orgId, fromOrders);
		this.priceid = priceid;
	}
	
	@Override
	protected SaleItem createSaleItem() {
		return new SaleItem(){
			
			/***
			 *Что бы не парится с текущим документом берем для подсчета среднего период от начала текущего дня минус 2 месяца 
			*/
			@Override
			public int calcQty(int offTakeCoef) {
				Date end = Util.getDate();
				Calendar c = Calendar.getInstance();
				c.setTime(end);
				c.add(Calendar.MONTH, -2);
				Date begin = c.getTime();
				DatePeriod dp = new DatePeriod(begin, end);
				com.grsoft.napoleon.documents.DocList orders = OrderDoc.instance().docList(id, "created", dp);
				
				int qty = 0;
				
				for(Document<?> d : orders){
					OrderImpl o = (OrderImpl)d;
					
					for(OrderItem i : o.getData().items)
						if(i.id.equals(priceid))
							qty += i.qty;
				}
				
				int cnt = orders.getCount();
				return cnt == 0 ? 0 : qty / cnt;
			}
		};
	}
}
