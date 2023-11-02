package com.grsoft.dataobjects.impl;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceWhData;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderImplEx extends OrderImpl {

	int curSkladIndex = 0;
	
	public int getItemValue(Price price, int index) {
		PriceEx pe = (PriceEx)price;
		return index == 0 || index > pe.whQty.size() ? price.qty : pe.whQty.get(index-1).qty;
	}

	public int getItemRes(Price price, int index) {
		PriceEx pe = (PriceEx)price;
		return index == 0 || index > pe.whQty.size() ? pe.res : ((PriceWhData)pe.whQty.get(index-1)).res;
	}

	public int getItemRes(Price price) {
		PriceEx pe = (PriceEx)price;
		return pe.res;
	}
	
	public void setCurSklad(int idx) {
		curSkladIndex = idx;
	}
	
	@Override
	protected int checkPriceQty(PriceImpl p, int qty, OrderItem item) {
		int newQty = qty;			
		if( ((CfgNplW)ConfigManager.getConfig()).checkPrice ) {
			int whQty = getItemValue(p.getData(), curSkladIndex);
			int priceQty = whQty;
			if( item != null ) {
				OrderItemEx oie = (OrderItemEx)item;
				if(curSkladIndex != oie.skladIndex) {
					updatePrice((PriceEx) p.getData(), item.qty, oie.skladIndex);
					item.qty = 0;
				} else {
					priceQty += item.qty;
				}
			}
			
			if( priceQty < qty ) {
				if( whQty < 0 ) newQty = 0;
				else newQty = priceQty;
			}
		}
		
		return newQty;
	}
	
	void updatePrice(PriceEx pe, int qty, int skladIndex) {
		if(skladIndex > 0 && skladIndex <= pe.whQty.size()) {
			int wqty = pe.whQty.get(skladIndex - 1).qty;
			pe.whQty.get(skladIndex - 1).qty = wqty + qty;
		} else {
			pe.qty += qty;
		}
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		updatePrice((PriceEx)price.getData(),qty, curSkladIndex);
		price.write();
	}
	
	@Override
	protected void prepareDeleteItem(PriceImpl pi, OrderItem item) {
		curSkladIndex = ((OrderItemEx)item).skladIndex;
		super.prepareDeleteItem(pi, item);
	}

	public boolean hasDifferentQtyInDelivery(){
		String where = "created=" + Long.toString(data.created.getTime());
		List<Delivery> list = DbReader.fetch(Delivery.class, where);

		if (list.size() > 0){
			Delivery dlv = list.get(0);

			if (dlv.items.size() != data.items.size())
				return true;

			Map<String, Integer> map = new HashMap<>();
			for (DeliveryItem i : dlv.items)
				map.put(i.id, i.qty);

			for (OrderItem i : data.items){
				if (!map.containsKey(i.id))
					return true;

				if (map.get(i.id) != i.qty)
					return true;
			}
		}

		return false;
	}
}
