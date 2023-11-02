package com.grsoft.napoleon.documents;

import android.app.Activity;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.R;

public class OrderDocEx extends OrderDoc {
	
	@Override
	public void refreshDocSum() throws RuntimeException {}
	public void refreshDocSum(String orgId) {};
	
	protected OrderDocEx(String docName, String objName, Class<? extends OrderImplBase<? extends Order>> type) { 
		super(docName, objName, type);
	} 
	
	static public DocType instance(Class<? extends OrderImplBase<? extends Order>> type) {
		instance = new OrderDocEx("Заявки", "Order", type);
		return instance;
	}
	
	public void updateTotalSum(Activity activity, int sum, int weight, int count){
		updateTotalSum(activity, 0, weight, count, R.id.tvTotalSum);
	}
}
