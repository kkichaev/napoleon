package com.grsoft.dataobjects.impl;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.ActionDataItem;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.CostStrategyEx;
import com.grsoft.napoleon.DiscountHelper;
import com.grsoft.napoleon.TrdActionList;
import com.grsoft.napoleon.documents.OrderDoc;


public class OrderImplEx extends OrderImpl{
	
	
	public OrderImplEx() {
		updateQtyHandler = new UpdateQtyHandler() {

			@Override
			public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
				if(isNewItem){
					PriceImpl pi = new PriceImpl();
					
					if(pi.read("id", item.id)){
						int d = DiscountHelper.getMaxDiscount(getId(), (PriceEx) pi.getData());
						((OrderItemEx)item).disc = d;
						((OrderItemEx)item).maxdisc = d;
					}
				}
				
			}};
	}
	
	@Override
	public DataObject findItem(String itemId) {
		if( data.items != null )
			for(OrderItem oi : data.items) {
				if( oi.id.compareTo(itemId) == 0 && ((OrderItemEx)oi).gift.length() == 0)
					return oi;
			}
		
		return null;
	}
	
	public void updateQty(PriceImpl priceImpl, int qty, int cost, String gift) {
		boolean inPack = false;
		Price price = priceImpl.getData();
		OrderItemEx item = (OrderItemEx) findUpdateItem(price.id, gift);

		int priceUpdate = 0;

		boolean needUpdate = true;
		if (item == null) // new item
		{
			if (qty > 0) {
				Class<? extends DataObject> itemClass = DataObjectInfo
						.getInstance().getListType(data.getClass(), "items");

				try {
					item = (OrderItemEx) itemClass.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}

				item.cost = cost;
				item.id = price.id;
				item.qty = qty;
				item.gift = gift;
				
				if (inPack)
					item.flags |= OrderItem.IN_PACK;

				if (updateQtyHandler != null)
					updateQtyHandler.itemUpdated(item, data, true);

				data.items.add(item);
				priceUpdate = -qty;
			} else
				needUpdate = false;
		} else {
			priceUpdate = item.qty;

			if (qty == 0) {
				data.items.remove(item);
			} else {
				priceUpdate -= qty;

				if (item.qty != qty) {
					item.qty = qty;
					item.cost = cost;
					if (inPack)
						item.flags |= OrderItem.IN_PACK;
					else
						item.flags &= (~OrderItem.IN_PACK);
				} else if (item.cost != cost) {
					item.cost = cost;
				} else
					needUpdate = false;

				if (updateQtyHandler != null) {
					updateQtyHandler.itemUpdated(item, data, false);
					needUpdate = true;
				}
			}
		}

		if (needUpdate) {
			if (qty != 0)
				beforeItemWrite(item, price);

			write();
			if (priceUpdate != 0 && checkPriceQty())
				updatePrice(priceImpl, priceUpdate);

			// refresh sum after writing
			getDocumentType().refreshDocSum(data.id);
		}
	}

	public OrderItemEx findUpdateItem(String id, String gift) {
		if (data.items != null)
			for (OrderItem oi : data.items) {
				OrderItemEx oiex = (OrderItemEx) oi;
				if (oi.id.compareTo(id) == 0 && oiex.gift.equals(gift))
					return oiex;
			}

		return null;
	}
	
	@Override
	public void open(Context context) {
		if(isEditable())
			TrdActionList.open(context, this);
		else
			super.open(context);
	}

	public int getDisc(Price p) {
		int result = 0;
		
		OrderItemEx i = (OrderItemEx) findItem(p.id);
		if(i != null)
			result = i.disc;
		else
			result = DiscountHelper.getMaxDiscount(getId(), (PriceEx) p);
		
		return result;
	}

	public void setDisc(Price p, int disc, int maxdisc) {
		OrderItemEx i = (OrderItemEx) findItem(p.id);
		if(i != null){
			i.disc = disc;
			i.maxdisc = maxdisc;
			int c = ((CostStrategyEx)CostStrategy.getInstance(getClass())).getBaseItemCost(p, this);
			i.cost = DiscountHelper.calcDisc(c, disc);
			OrderDoc.instance().refreshDocSum(getId());
			write();
			close();
		}
	}

	public void updateActions(List<ActionDataItem> items) {
		List<OrderItem> rmv = new ArrayList<OrderItem>();
		for(OrderItem oi : data.items) {
			if(((OrderItemEx)oi).IsActionItem())
				rmv.add(oi);
		}
		
		data.items.removeAll(rmv);
		for(ActionDataItem adi : items) {
			if(adi.qty > 0) {
				OrderItemEx oie = new OrderItemEx(adi);
				data.items.add(oie);
			}
		}
		if(rmv.size() > 0 || items.size() > 0) {
			write();
			OrderDoc.instance().refreshDocSum(data.id);
		}
	}
}
