package com.grsoft.dataobjects;

public class OrderEx extends Order implements OrderBase {
	public String payType;

	@Override public String getPayType() { return payType; }
	@Override public void setPayType(String newPayType) { payType = newPayType; }

	@Override public String getFirm() { return firmCode; }
	@Override public void setFirm(String newFirm) { firmCode = newFirm; }
}
