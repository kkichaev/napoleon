package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceQtyItem;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;

import java.util.List;

public class OrderImplEx extends OrderImpl {
	private int getSkladsItemValue(Price item, int whIndex) {
		List<PriceQtyItem> whQty = item.whQty;

		return ( whIndex == 0 || whIndex > whQty.size() ) ?  item.qty : whQty.get(whIndex-1).qty;
	}

	public int getItemQty(Price item, int wh) {
		OrderItem oi = (OrderItem) findItem(item.id, wh);

		if (oi == null)
			return 0;
		else
			return oi.qty;
	}

	public DataObject findItem(String id, int wh) {
		if(data.items != null) {
			for(OrderItem oi : data.items) {
				if( oi.id.compareTo(id) == 0 && ((OrderItemEx)oi).whIdx == wh)
					return oi;
			}
		}
		
		return null;
	}
	
	protected int checkPriceQty(PriceImpl p, int qty, OrderItem item, int whIdx) {
		int newQty = qty;
		int whQty = getSkladsItemValue(p.getData(), whIdx);
		
		if( ((CfgNplW)ConfigManager.getConfig()).checkPrice ) {
			int priceQty = whQty;
			if( item != null ) priceQty += item.qty;
			
			if( priceQty < qty ) {
				if( whQty < 0 ) newQty = 0;
				else newQty = priceQty;
			}
		}
		
		return newQty;
	}
	
	protected DataObject findUpdateItem(Price price, int whIdx) {
		return findItem(price.id, whIdx);
	}

	public boolean updateQty(PriceImpl priceImpl, int qty1, int qty2, int cost, boolean inPack1, boolean inPack2) {
		boolean ret = true;
		
		if (!updateQtyItem(priceImpl, qty1, 0, cost, inPack1))
			ret = false;
		
		if (!updateQtyItem(priceImpl, qty2, 1, cost, inPack1))
			ret = false;
		
		getDocumentType().refreshDocSum(data.id);
		
		return ret;
	}
	
	private boolean updateQtyItem(PriceImpl priceImpl, int qty, int whIdx, int cost, boolean inpack) {
		boolean ret = true;
		Price price = priceImpl.getData();
		
		OrderItem item = (OrderItem) findUpdateItem(price, whIdx);

		if( checkPriceQty() ) {
			int newQty = checkPriceQty(priceImpl, qty, item, whIdx);
			if( newQty != qty ) {
				ret = false;			
				qty = newQty;
			}
		}

		if (updateQtyInWh(priceImpl, (OrderItemEx)item, qty, whIdx, cost, inpack))
			write();
		
		return ret;
	}
	
	private boolean updateQtyInWh(PriceImpl price, OrderItemEx item, int qty, int whIdx, int cost, boolean inpack) {
		int priceUpdate = 0;
		boolean res = true;
		
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
			
				item.cost = cost;
				item.id = price.getData().id;
				item.qty = qty;
				item.whIdx = whIdx;
				
				if(inpack) 
					item.flags |= OrderItem.IN_PACK;
		
				data.items.add(item);
				priceUpdate = - qty;
			} else
				res = false;
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
					item.cost = cost;
					if(inpack) item.flags |= OrderItem.IN_PACK;
					else item.flags &= (~OrderItem.IN_PACK);
				} else if( item.cost != cost ) {
					item.cost = cost;					
				} else if (item.inPack() != inpack){
					if(inpack) item.flags |= OrderItem.IN_PACK;
					else item.flags &= (~OrderItem.IN_PACK);
				}else
					res = false;
			}
		}
		
		if( res && priceUpdate != 0 && checkPriceQty() ) {
			if (whIdx == 0)
				price.updateQty(priceUpdate);
			else if (whIdx <= price.getData().whQty.size()) {
				PriceQtyItem wd = (price.getData()).whQty.get(whIdx-1);
				wd.qty  += priceUpdate;
				price.write();
			}
		
			price.close();
		}
		
		return res;
	}
}
