package com.grsoft.dataobjects.impl;

import java.util.Calendar;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.WHQty;
import com.grsoft.dataobjects.WHQtyItem;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;
import com.grsoft.util.Util;

public class OrderImplEx extends OrderImpl {
	WHQty qtyData = null; 
	
	@Override
	public int weight() {
		int weight = 0;
		
		if( !Features.NO_WEIGHT_IN_ORDER && data.items != null ) {
			PriceImpl p = new PriceImpl();
			p.setReadingFields("weight");
			
			Price pd = p.getData();
			for (OrderItem item: data.items) {
				pd.id = item.id;
				
				if( ((OrderItemEx)item).inKG > 0  )
					weight += item.qty;
				else  if( p.read() )
					weight += FPOperation.itemMul(item.qty, pd.weight, Consts.QTY_SCALE);
			}
			p.close();
		}
		
		return weight;
	}
	
	@Override
	public int getItemValue(Price item) {
		if(qtyData == null) {
			WHQtyImpl whi = new WHQtyImpl();
			qtyData= whi.getData();
			qtyData.idwh = ((OrderEx)data).whCode;
			whi.read();
			whi.close();
		}
		
		int qty = 0;
		for(WHQtyItem witem : qtyData.items) {
			if(witem.id.equals(item.id)) {
				qty = witem.qty;
				break;
			}
		}
		return qty;
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		String pid = price.getData().id;
		
		WHQtyImpl whi = new WHQtyImpl();
		WHQty whq = whi.getData();
		whq.idwh = ((OrderEx)data).whCode;
		if(whi.read()) {
			for(WHQtyItem item : whq.items) {
				if(item.id.equals(pid)) {
					item.qty += qty;
					whi.write();
					qtyData = null;
					break;
				}
			}
		}
		whi.close();
	}

	@Override
	protected void postCopyProcess(CreatableDocument<Order> o) {
		Calendar c = Calendar.getInstance();
		c.setTime(Util.getDate());
		c.add(Calendar.DAY_OF_MONTH, 1);
		o.getData().date = c.getTime();
	}
}
