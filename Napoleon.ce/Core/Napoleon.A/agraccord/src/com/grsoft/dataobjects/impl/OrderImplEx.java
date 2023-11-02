package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.WhQty;
import com.grsoft.util.Consts;

public class OrderImplEx extends OrderImpl {
	@Override
	public int getItemValue(Price item) {
		int idx = ((OrderEx)data).whIndex; 
		if(idx == 0)
			return item.qty;
		
		return ((PriceEx)item).whQty.get(idx-1).qty;
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		int idx = ((OrderEx)data).whIndex; 
		if(idx == 0)
			super.updatePrice(price, qty);
		else {
			PriceEx pe = (PriceEx)price.data;
			if( idx-- <= pe.whQty.size() ) { 
				WhQty wq = new WhQty();
				wq.qty = pe.whQty.get(idx).qty + qty;
				pe.whQty.set(idx, wq);
				price.write();
			}
		}
	}
	
	@Override
	public void updateItemsCost(int sumType) {
		Order order = getData();
		order.sumType = sumType;
		PriceImpl priceImpl = new PriceImpl();
		Price p = priceImpl.getData();
		try{
			for(OrderItem item : order.items){
				p.id = item.id;
				if (priceImpl.read()) {
					OrderItemEx oe = (OrderItemEx)item;
					int cost = (p.cost.size() > sumType && sumType >= 0) ? p.cost.get(sumType).cost : 0;			
					oe.cost = cost;
					oe.discount = 0;
				}
			}
			
			if (write() != Consts.INVALID_ID)
				getDocumentType().refreshDocSum(order.id);
		}catch(Exception e){ 
			e.printStackTrace();
		}finally{
			priceImpl.close();
		}
	}
}
