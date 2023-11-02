package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;

public class OrderImplEx extends OrderImpl {
	
	@Override
	public int getItemValue(Price item) {
		PriceEx pe = (PriceEx)item;
		int whIndex = ((OrderEx)data).whIndex;
		
		if( whIndex <= 0 || whIndex > pe.whQty.size())
			return super.getItemValue(item);
		
		return pe.whQty.get(whIndex-1).qty;
	}

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost,
			boolean inPack) {
		Price price = priceImpl.getData();
		boolean ret = true;
		OrderItem item = (OrderItem) findUpdateItem(price);

		int priceUpdate = 0;
		if (checkPriceQty()) {
			int newQty = checkPriceQty(priceImpl, qty, item);
			if (newQty != qty) {
				ret = false;
				qty = newQty;
			}
		}

		boolean needUpdate = true;
		if (item == null) // new item
		{
			Class<? extends DataObject> itemClass = DataObjectInfo
					.getInstance().getListType(data.getClass(), "items");

			try {
				item = (OrderItem) itemClass.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}

			item.cost = cost;
			item.id = price.id;
			item.qty = qty;

			if (inPack)
				item.flags |= OrderItem.IN_PACK;

			if (updateQtyHandler != null)
				updateQtyHandler.itemUpdated(item, data, true);

			if (qty > 0 || ((OrderItemEx) item).qty2 > 0) {
				data.items.add(item);
				priceUpdate = -qty;
			} else
				needUpdate = false;
		} else {
			priceUpdate = item.qty;

			if (qty == 0 && ((OrderItemEx)item).qty2 == 0) {
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

		return ret;
	}
	
	protected void updatePrice(PriceImpl price, int qty) {
		PriceEx pe = (PriceEx)price.getData();
		int whIndex = ((OrderEx)data).whIndex;
		
		if( whIndex <= 0 || whIndex > pe.whQty.size())
			pe.qty += qty;
		else
			pe.whQty.get(whIndex-1).qty += qty;
		
		price.write();
	}
}
