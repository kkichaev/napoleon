package com.grsoft.dataobjects;

public class OrderEx extends Order implements OrderExtended{
	public String dogCode = "";

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
		// TODO Auto-generated method stub
		return false;
	}
}
