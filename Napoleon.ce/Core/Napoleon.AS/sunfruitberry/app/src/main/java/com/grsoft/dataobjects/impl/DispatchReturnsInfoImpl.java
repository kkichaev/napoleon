package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DispatchReturnsInfo;
import com.grsoft.dataobjects.DispatchReturnsItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.DispatchReturnsView;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.gps.GPSUtilNew;

import android.content.Context;

public class  DispatchReturnsInfoImpl extends CreatableDocument<DispatchReturnsInfo> {

	@Override public void open(Context context) { DispatchReturnsView.open(context, this); }

	
	@Override
	public String getDescription(Context context) {
		if(data.newDoc > 0)
			return "Новый документ";
		if(data.createdOrder != 0)
			return "Создан заказ";
		return "";
	}
	
	public OrderImpl createOrder() {
		OrderImpl ret = (OrderImpl) OrderDoc.instance().create();
		
		ret.initSilent(data.id, GPSUtilNew.getLastKnownLocation());
//		CostStrategy cs = CostStrategy.getInstance(ret.getClass());
		
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();
		
		for(DispatchReturnsItem dri : data.items) {
			p.id = dri.id;
			if(pi.read()) {
				//int cost = cs.getItemCost(p, ret);
				ret.updateQty(pi, dri.qty, dri.cost, false);
			}
		}
		
		data.createdOrder = ret.getData().created.getTime();
		
		write();
		ret.write();
		pi.close();
		ret.close();
		
		return ret;
	}
	
	@Override public void setProceeded() {}
	@Override public void unsetProceeded() { super.unsetProceeded(); }
	
	@Override public boolean isExported() { return true; }
	@Override public boolean isProceeded() { return true; }


	public void markReaded() {
		data.newDoc = 0;
		write();
	}
}
