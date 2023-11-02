package com.grsoft.napoleon.documents;

import android.app.Activity;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.R;

public class ReturnDocEx extends ReturnDoc {

	protected ReturnDocEx(
			Class<? extends OrderImplBase<? extends Order>> retClass) {
		super(retClass);
	}
	
	@Override
	public void refreshDocSum() throws RuntimeException {}
	public void refreshDocSum(String orgId) {};
	public void updateTotalSum(Activity activity, int sum, int weight, int count){
		updateTotalSum(activity, 0, weight, count, R.id.tvTotalSum);
	}
	
	static public DocType instance(Class<? extends OrderImplBase<? extends Order>> type) {
		instance = new ReturnDocEx(type);
		return instance;
	}
}
