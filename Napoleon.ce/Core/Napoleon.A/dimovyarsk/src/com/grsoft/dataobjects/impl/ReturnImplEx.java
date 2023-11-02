package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.AgentPrefixEx;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.napoleon.ReturnCount;

import android.content.Context;

public class ReturnImplEx extends ReturnImpl {
	Integer index = null;
	PriceImpl pi = new PriceImpl();
	
	@Override
	public void editItem(long itemRowid, Context context) {
		ReturnCount.open(context, this, itemRowid);
	}

	public void editItem(OrderItem item, Context context) {
		ReturnCount.openByIndex(context, this, data.items.indexOf(item));
	}
	
	@Override
	public long write() {
		int ord = 1;
		HashMap<String, ReturnItem> items = new HashMap<String, ReturnItem>();
		List<ReturnItem> rmv = new ArrayList<ReturnItem>();
		for(OrderItem oi : data.items) {
			ReturnItem ri = (ReturnItem)oi; 
			String key = ri.getKey();
			ReturnItem fnd = items.get(key);
			if(fnd == null) {
				items.put(key, ri);
				ri.num = ord++;				
			} else  {
				fnd.qty += ri.qty;
				rmv.add(ri);
			}
		}
		data.items.removeAll(rmv);
		return super.write();
	}
	
	@Override
	public void close() {
		pi.close();
		super.close();
	}
	
	@Override
	protected DataObject findUpdateItem(Price price) {
		return (index == null || index == -1) ? super.findUpdateItem(price) : data.items.get(index);
	}
	
	public boolean updateQty(OrderItem oi, int qty, int cost, boolean inPack) {
		index = data.items.indexOf(oi);
		Price p = pi.getData();
		p.id = oi.id;
		pi.read();
		
		return updateQty(pi, qty, cost, inPack);
	}
	
	@Override
	public void postInit() {
		super.postInit();
		
		AgentPrefixEx ae = (AgentPrefixEx) AgentPrefix.get();
		if( ae != null )
			data.supplyer = ae.firma;
	}
}
