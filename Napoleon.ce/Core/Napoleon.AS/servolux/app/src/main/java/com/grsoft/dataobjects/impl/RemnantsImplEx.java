package com.grsoft.dataobjects.impl;

import java.util.UUID;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.RmntSalesPlaceQty;
import com.grsoft.napoleon.RemnantsDocEdit;

import android.content.Context;

public class RemnantsImplEx extends RemnantsImpl {
	@Override
	public void open(Context context) {
		RemnantsDocEdit.open(context, this);
	}
	
	@Override
	protected void openPrice(Context context) {
		RemnantsDocEdit.open(context, this);
	}
	
	public int getQty(String priceId, String salesType) {
		int ret = 0;
		
		RemnantItemEx ri = (RemnantItemEx) findItem(priceId);
		if(ri != null) {
			for(RmntSalesPlaceQty sq : ri.items) {
				if(sq.id.equals(salesType)) {
					ret = sq.qty;
					break;
				}
			}
		}
		
		return ret;
	}
	
	@Override public int getItemQty(Price price) { return countQty(price.id);	}
	
	public int countQty(String id) {
		RemnantItemEx ri = (RemnantItemEx) findItem(id);
		return ri == null ? 0 : ri.countQty();
	}
	
	public void setQty(int qty, Price price, String salesType) {
		RemnantItemEx ri = (RemnantItemEx) findItem(price.id);
		if(ri == null) {
			ri = new RemnantItemEx();
			ri.id = price.id;
			ri.qty = 0;
			ri.uid = UUID.randomUUID().toString().replace("-", "");
			data.items.add(ri);
		}
		
		boolean done = false;
		for(RmntSalesPlaceQty sq : ri.items) {
			if(sq.id.equals(salesType)) {
				if(qty == 0)
					ri.items.remove(sq);
				else
					sq.qty = qty;
				done = true;
				break;
			}
		}
		if(!done && qty != 0) {
			RmntSalesPlaceQty sq = new RmntSalesPlaceQty();
			sq.id = salesType;
			sq.qty = qty;
			ri.items.add(sq);
		}
		if(ri.items.size() == 0)
			data.items.remove(ri);
		else
			ri.qty = ri.countQty();
		write();
	}
}
