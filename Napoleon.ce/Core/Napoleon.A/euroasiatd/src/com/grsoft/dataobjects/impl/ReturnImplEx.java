package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.napoleon.ReturnDetailEx;
import com.grsoft.napoleon.ReturnPriceCount;

import android.content.Context;

public class ReturnImplEx extends ReturnImpl {
	
	boolean forceNewItem = false;
	
	@Override
	public void editItem(long itemRowid, Context context) {
		ReturnPriceCount.open(context, itemRowid, this, false);
	}
	
	@Override
	public void open(Context context) {
		ReturnDetailEx.open(context, this);
	}

	public void setNewItem() {
		forceNewItem = true;
	}
	
	@Override
	protected DataObject findUpdateItem(Price price) {
		return forceNewItem ? null : super.findUpdateItem(price);
	}
	
	@Override
	public long sum() {
		if((data.params & ParamState.ofProceeded) == ParamState.ofProceeded)
			return ((ReturnEx)data).commitSum;
		return super.sum();
	}
	
	@Override
	public String getDescription(Context context) {
		if((data.params & ParamState.ofProceeded) == ParamState.ofProceeded)
			return "утвержден";
		return super.getDescription(context);
	}
	
}
