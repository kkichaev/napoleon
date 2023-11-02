package com.grsoft.dataobjects;


public class PaymentEx extends Payment implements Skladable{
	public String skladid = "";
	
	@Override public String getSkladId() { return skladid; }
}
