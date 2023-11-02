package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Storcheck;
import com.grsoft.dataobjects.StorcheckItem;
import com.grsoft.napoleon.StorcheckEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;

import android.content.Context;
import android.graphics.Color;

public class StorcheckImpl extends CreatableDocument<Storcheck> implements Itemsable {

	@Override
	public void open(Context context) {
		StorcheckEdit.open(context, this);
	}

	@Override
	public void editItem(long itemRowid, Context context) {
		PriceImpl pi = new PriceImpl();
		pi.read(itemRowid);
		pi.close();
		
		updateQty(pi, 0, 0, false);		
	}

	@Override
	public DataObject findItem(String itemId) {
		for(StorcheckItem i : data.items)
			if(i.id.equals(itemId))
				return i;
		return null;
	}

	@Override
	public int getItemColor() {return Color.GREEN; }

	@Override
	public int getItemValue(Price item) { return item.qty; }

	@Override
	public int getItemQty(Price item) { return findItem(item.id) == null ? 0 : Consts.QTY_SCALE; }

	@Override public long getItemSum(Price item) { return 0; }

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		if(isEditable() == false)
			return true;
		
		String itemId = priceImpl.getData().id;
		
		boolean finded = false;
		for(StorcheckItem i : data.items)
			if(i.id.equals(itemId)) {
				data.items.remove(i);
				finded = true;
				break;
			}
		
		if(!finded) {
			StorcheckItem si = new StorcheckItem();
			si.id = itemId;
			data.items.add(si);
		}
		write();
		return true;
	}
}
