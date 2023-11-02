package com.grsoft.dataobjects;


public class DeliveryEx extends Delivery implements Skladable{
	public String skladid = "";

	@Override public String getSkladId() { return skladid; }
}
