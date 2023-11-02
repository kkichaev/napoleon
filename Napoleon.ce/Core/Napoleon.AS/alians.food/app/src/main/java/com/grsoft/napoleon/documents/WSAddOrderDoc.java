package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.WSAddOrderImpl;
import com.grsoft.dataobjects.impl.WSOrderImpl;
import com.grsoft.napoleon.R;

import android.app.Activity;
import android.view.View;
import android.widget.Adapter;

public class WSAddOrderDoc extends OrderDoc {
	static public final String DOC_NAME = "ДопЗагруз на борт";
	static public final String OBJ_NAME = "AddOrderCharge";
	static protected WSAddOrderDoc instance = null;
	
	protected WSAddOrderDoc(Class<? extends OrderImplBase<? extends Order>> retClass) { 
		super(DOC_NAME, OBJ_NAME, retClass); }
	
	static public DocType instance() {
		if( instance == null ) {
			instance = new WSAddOrderDoc(WSAddOrderImpl.class);
		}
		return instance;
	}
	
	@Override public int getResurceId() { return R.drawable.wsorder; }
	
	@Override
	public int getDocTitle() {
		return R.string.wsorder_title;
	}
	
	public void updateTotalSum(Activity activity, int sum, int weight, int count){
		updateTotalSum(activity, 0, weight, count, R.id.tvTotalSum);
	}
	
	@Override
	public void refreshDocSum() throws RuntimeException {}
	public void refreshDocSum(String orgId) {};
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		super.setView(adapter, view, doc);
		view.findViewById(R.id.tvSum).setVisibility(View.GONE);
	}
	
	public int getResurce2Id() {
		return getResurceId();
	};
}
