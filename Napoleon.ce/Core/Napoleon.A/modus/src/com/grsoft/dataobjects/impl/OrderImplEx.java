package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.ActionPrice;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.util.ExtrasConst;

public class OrderImplEx extends OrderImpl {
	public OrderItem findItemAction(String id) {
		for(OrderItem item : data.items)
			if (((OrderItemEx)item).action == 1 && item.id.equals(id))
				return item;
		
		return null;
	}
	
	public OrderItem findItemNonAction(String id) {
		for(OrderItem item : data.items)
			if (((OrderItemEx)item).action == 0 && item.id.equals(id))
				return item;
		
		return null;
	}
	
	public int checkActionQty(ActionPriceImpl a, int qty, OrderItem item) {
		int newQty = qty;
		int whQty = a.getData().qty;
		
		if( true ) {
			int priceQty = whQty;
			if( item != null ) priceQty += item.qty;
			
			if( priceQty < qty ) {
				if( whQty < 0 ) newQty = 0;
				else newQty = priceQty;
			}
		}
		
		return newQty;
	}
	
	@Override
	public DataObject findItem(String id) {
		OrderItem itemAction = findItemAction(id);
		OrderItem item = findItemNonAction(id);
		
		return itemAction != null ? itemAction : item;
		
	}
	
	protected DataObject findUpdateItem(Price price) {
		for(OrderItem item : data.items)
			if (((OrderItemEx)item).action == 0 && item.id.equals(price.id))
				return item;
		
		return null;
	}
	
	public boolean updateQty(PriceImpl priceImpl, ActionPriceImpl actionImpl, int inputqty, int cost, boolean inPack) {
		int qty = 0;
		
		if (actionImpl.getRowid() != ExtrasConst.INVALID_ROWID) {
			qty = inputqty;
			ActionPrice action = actionImpl.getData();
			String priceid = action.priceid;
			OrderItemEx item = (OrderItemEx) findItemAction(priceid);
	
			if( checkPriceQty() ) {
				int newQty = checkActionQty(actionImpl, qty, item);
				if( newQty != qty ) {
					qty = newQty;
				}
			}
	
			int priceUpdate = 0;
			boolean needUpdate = true;
			
			if( item == null ) // new item
			{
				if( qty > 0 )
				{
					Class <? extends DataObject> itemClass = DataObjectInfo.getInstance().getListType(data.getClass(), "items");
	
					try {
						item = (OrderItemEx) itemClass.newInstance();
					} catch (Exception e) {
						e.printStackTrace();
					}
				
					item.cost = action.cost;
					item.id = action.priceid;
					item.qty = qty;
					item.action = 1;
					
					if(inPack) item.flags |= OrderItem.IN_PACK;
			
					data.items.add(item);
					priceUpdate = - qty;
				} else
					needUpdate = false;
			} else
			{
				priceUpdate = item.qty;
				
				if( qty == 0 ) {
					data.items.remove(item);
				}
				else {
					priceUpdate -= qty;
					
					if( item.qty != qty ) {
						item.qty = qty;
						item.cost = action.cost;
						if(inPack) item.flags |= OrderItem.IN_PACK;
						else item.flags &= (~OrderItem.IN_PACK);
					} else if( item.cost != action.cost ) {
						item.cost = action.cost;					
					} else if (item.inPack() != inPack){
						if(inPack) item.flags |= OrderItem.IN_PACK;
						else item.flags &= (~OrderItem.IN_PACK);
					}else
						needUpdate = false;
				}
			}
			
			if( needUpdate ) {
				write();
				
				if( priceUpdate != 0 && checkPriceQty() ) {
					action.qty += priceUpdate;
					actionImpl.write();
					actionImpl.close();
				}
				
				// refresh sum after writing
				getDocumentType().refreshDocSum(data.id);
			}
		
		}
		
		return updateQty(priceImpl, inputqty - qty, cost, inPack);
	}
}
