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

public class OrderImplEx extends OrderImpl{
	public boolean minCostError = false;
	
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
