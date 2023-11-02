package com.grsoft.napoleon.documents;

import android.app.Activity;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.WSOrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.R;

public class WSOrderDoc extends OrderDoc {
	static public final String DOC_NAME = "Заявка сети";
	static public final String OBJ_NAME = "OrderW";
	static protected WSOrderDoc instance = null;
	
	protected WSOrderDoc(Class<? extends OrderImplBase<? extends Order>> retClass) { 
		super(DOC_NAME, OBJ_NAME, retClass); }
	
	static public DocType instance() {
		if( instance == null ) {
			instance = new WSOrderDoc(WSOrderImpl.class);
		}
		return instance;
	}
	
	@Override public int getResurceId() { return R.drawable.wsorder; }
	
	@Override public int getDocTitle() { return R.string.hide_order_title; }
	
	public void updateTotalSum(Activity activity, int sum, int weight, int count){
		updateTotalSum(activity, 0, weight, count, R.id.tvTotalSum);
	}
	
	@Override
	public void refreshDocSum() throws RuntimeException {}
	public void refreshDocSum(String orgId) {};
}
