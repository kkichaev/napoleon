package com.grsoft.dataobjects.impl;

import java.util.Date;

import android.content.Context;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.ExchangeDoc;
import com.grsoft.dataobjects.ExchangeItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.ExchangeCount;
import com.grsoft.napoleon.ExchangeDetail;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;

public class ExchangeImpl extends CreatableDocument<ExchangeDoc> implements Itemsable {

	@Override
	public void open(Context context) {
		ExchangeDetail.open(context, this);
	}

	@Override
	public void editItem(long itemRowid, final Context context) {
		ExchangeCount.open(context, this, itemRowid);
	}

	@Override
	public DataObject findItem(String itemId) {
		if( data.items != null )
			for(ExchangeItem ri : data.items) {
				if( ri.id.compareTo(itemId) == 0 )
					return ri;
			}
		
		return null;
	}

	@Override
	public int getItemColor() { return com.grsoft.napoleon.R.color.magneta; }

	@Override
	public int getItemValue(Price item) { return item.qty; }

	@Override
	public int getItemQty(Price item) {
		ExchangeItem ri = (ExchangeItem) findItem(item.id);		
		return ri == null ? 0 : ri.qty;
	}

	@Override
	public long getItemSum(Price item) { return 0; }
	
	public boolean updateQty(PriceImpl priceImpl, int qty, Date date) {
		Price price = priceImpl.getData();
		ExchangeItem item = (ExchangeItem) findItem(price.id);

		boolean needUpdate = true;
		if( item == null ) {
			if( qty > 0 ) {
				item = new ExchangeItem();
				item.id = price.id;
				item.qty = qty;
				item.date = date;
				data.items.add(item);
			}
			else
				needUpdate = false;
		} else {
			if( qty == 0 )
				data.items.remove(item);
			else {
				if( item.qty != qty ) {
					item.qty = qty;
					item.date = date;
				} else
					needUpdate = false;
			}
		}
		
		if( needUpdate )
			write();
		
		return needUpdate;
	}

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		return updateQty(priceImpl, qty, new Date());
	}
}
