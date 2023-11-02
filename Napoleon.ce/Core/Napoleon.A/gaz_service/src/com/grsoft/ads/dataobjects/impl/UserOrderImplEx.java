package com.grsoft.ads.dataobjects.impl;

import com.grsoft.ads.dataobjects.Order;
import com.grsoft.ads.dataobjects.UserOrderEx;
import com.grsoft.util.Util;

public class UserOrderImplEx extends UserOrderImpl 
implements OrderExtended{
	public boolean isDoing(){
		return (getData().params & Order.DOING_PARAMS) == Order.DOING_PARAMS;
	}
	
	public void setDoing(){
		getData().params = Order.DOING_PARAMS;
		((UserOrderEx)getData()).begin = Util.getDateTime();
	}
	
	public boolean isDone(){
		return (getData().params & Order.DONE_PARAMS) == Order.DONE_PARAMS;
	}
	
	public void setDone(){
		getData().params = Order.DONE_PARAMS;
		((UserOrderEx)getData()).end = Util.getDateTime();
	}
	
	@Override
	public boolean isEditable() {
		return super.isEditable() && !isDone();
	}

	@Override
	public String getCertificate() {
		return ((UserOrderEx)getData()).certificate;
	}

	@Override
	public String getProtocol() {
		return ((UserOrderEx)getData()).protocol;
	}
}
