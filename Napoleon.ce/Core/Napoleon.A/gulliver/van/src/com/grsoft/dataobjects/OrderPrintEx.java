package com.grsoft.dataobjects;

public class OrderPrintEx extends OrderPrint 
implements OrderExtended{
	public String dogCode = "";
	public int isGenDoc = 0;

	@Override
	public String getFirmCode() {
		return firmCode;
	}

	@Override
	public String getDogCode() {
		return dogCode;
	}

	@Override
	public void setFirmCode(String value) {
		firmCode = value;
	}

	@Override
	public void setDogCode(String value) {
		dogCode = value;
	}

	@Override
	public boolean isGenDoc() {
		return isGenDoc > 0;
	}
}
