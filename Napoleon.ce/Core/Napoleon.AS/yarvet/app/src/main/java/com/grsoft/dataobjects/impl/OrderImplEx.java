package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class OrderImplEx extends OrderImpl {
	PriceImpl price = new PriceImpl();
	public boolean minCostError = false;
	
	@Override
	public int getItemValue(Price item) {
		PriceEx pe = (PriceEx)item;
		int whIndex = ((OrderEx)data).whIndex;
		
		if( whIndex <= 0)
			return super.getItemValue(item);
		
		return whIndex > pe.whQty.size() ?  0 : pe.whQty.get(whIndex-1).qty;
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		int whIndex = ((OrderEx)data).whIndex;
		
		PriceEx pe = (PriceEx)price.getData();
		if( whIndex == 0 )
			super.updatePrice(price, qty);
		else if( whIndex <= pe.whQty.size() ) {
			pe.whQty.get(whIndex-1).qty += qty;
			price.write();
		}
	}

	public long getRentability(String id) {
		long res = 0;
		
		OrderItem i = (OrderItem) findItem(id);
		
		if (i != null) {
			if (price.read("id", id)) {
				long c = i.cost - ((PriceEx)price.getData()).minCost;
				
				if (i.cost != 0)
					res = (long) ((double)c / i.cost * 100 * Consts.SUM_SCALE);
			}
		}
		
		return res;
	}

	public void updateItemsCost(int sumType){
		Order order = getData();
		order.sumType = sumType;
		PriceImpl priceImpl = new PriceImpl();
		Price p = priceImpl.getData();

		CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) this.getClass());
		try{
			for(OrderItem item : order.items){
				p.id = item.id;
				((OrderItemEx)item).discItem = ((OrderEx)order).discOrd;
				if (priceImpl.read())
					item.cost = cs.getItemCost(p, this);

				if(item.cost < ((PriceEx)p).minCost){
					minCostError = true;
					return;
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
