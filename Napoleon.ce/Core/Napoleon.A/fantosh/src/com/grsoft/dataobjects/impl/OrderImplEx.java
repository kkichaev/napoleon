package com.grsoft.dataobjects.impl;

import java.util.ArrayList;

import android.content.Context;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceQtyItem;
import com.grsoft.napoleon.OrderItemEdit;

public class OrderImplEx extends OrderImpl {
	PriceQtyImpl priceQty = new PriceQtyImpl();
	
	@Override
	public void editItem(long itemRowid, Context context) {
		OrderItemEdit.open(context, itemRowid, (DbObject<Order>) this);
	}

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost,
			boolean inPack) {
		ArrayList<OrderItem> list = new ArrayList<OrderItem>();
		list.addAll(data.items);
		
		for(OrderItem i : list){
			OrderItemEx iex = (OrderItemEx)i;
			updateQty(priceImpl, qty, cost, iex.colorid, iex.sizeid);
		}
 		return true;
	}
	
	public void updateQty(PriceImpl priceImpl, int qty, int cost, int colorid,
			int sizeid) {
		boolean inPack = false;
		Price price = priceImpl.getData();
		OrderItemEx item = (OrderItemEx) findUpdateItem(price.id, colorid, sizeid);

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
				item.colorid = colorid;
				item.sizeid = sizeid;

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
				updatePrice(price.id, priceUpdate, colorid, sizeid);

			// refresh sum after writing
			getDocumentType().refreshDocSum(data.id);
		}
	}

	private void updatePrice(String id, int qty, int colorid, int sizeid) {
		priceQty.getData().id = id;
		priceQty.read();
		
		for(PriceQtyItem i : priceQty.getData().items)
			if(i.colorid == colorid && i.sizeid == sizeid){
				i.qty += qty;
				priceQty.write();
				break;
			}
		
		priceQty.close();
	}

	public OrderItemEx findUpdateItem(String id, int colorid, int sizeid) {
		if (data.items != null)
			for (OrderItem oi : data.items) {
				OrderItemEx oiex = (OrderItemEx) oi;
				if (oi.id.compareTo(id) == 0 && oiex.colorid == colorid
						&& oiex.sizeid == sizeid)
					return oiex;
			}

		return null;
	}
	
	@Override
	public int getItemValue(Price item) {
		int qty = 0;
		priceQty.getData().id = item.id;
		
		if(priceQty.read())
			for(PriceQtyItem i : priceQty.getData().items)
				qty += i.qty;
		
		priceQty.close();
		
		return qty;
	}

}
