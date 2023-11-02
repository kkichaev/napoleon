package com.grsoft.napoleon.util;

import android.widget.BaseAdapter;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;

public class QtyEditor extends InputNumber {

	BaseAdapter adapter;
	OrderImplBase<? extends Order> doc;
	OrderItem item;
	PriceImpl price = new PriceImpl();
	Price p;
	
	public QtyEditor(OrderImplBase<? extends Order> doc, String id, BaseAdapter adapter) {
		this.doc = doc;
		
		p = price.getData();
		p.id = id;
		price.read();
		price.close();

		this.item = (OrderItem) doc.findItem(id);
		this.adapter = adapter;
	}
	
	public QtyEditor(OrderImplBase<? extends Order> doc, OrderItem item, BaseAdapter adapter) {
		this.doc = doc;
		this.item = item;
		this.adapter = adapter;

		p = price.getData();
		p.id = item.id;
		price.read();
		price.close();
	}
	
	@Override
	public void applayInput(int value, Object... params) {
		@SuppressWarnings("unchecked")
		CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) doc.getClass());
		int cost = (item == null) ? cs.getItemCost(p, doc) : item.cost;
		boolean inPack = (Boolean)params[0];
		int qty = value;
//		if( inPack )
//			qty = (int)((long)value * p.qtyInPack / Consts.QTY_SCALE);
		if (doc.updateQty(price, qty, cost, inPack) )
			adapter.notifyDataSetChanged();
	}

	@Override
	public boolean isPackCanChange() {
		return (((PriceEx)p).packIn == 0);
	}

	@Override
	public boolean isInpack() {
		return item == null ?
				(((PriceEx)p).packIn > 0) || ((CfgNpl)ConfigManager.getConfig()).isPackView :
				item.inPack();
	}
	
	@Override public int getValue() { 
		return item == null ? 
				0 : 
				(int)(item.qty  / p.qtyInPack) * Consts.QTY_SCALE; 
	}
	
	public int getRest() {
		return item == null ? 0 : item.qty % p.qtyInPack;
	}
	
	@Override
	public boolean useComma() {
		return false;
	}

}
