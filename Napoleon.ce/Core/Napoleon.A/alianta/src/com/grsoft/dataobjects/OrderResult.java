package com.grsoft.dataobjects;

public class OrderResult extends DataObject {
	public String message = "";
	public String ordstatus = "";

	public boolean isFail() {
		return ordstatus.equals("fail");
	}
}
