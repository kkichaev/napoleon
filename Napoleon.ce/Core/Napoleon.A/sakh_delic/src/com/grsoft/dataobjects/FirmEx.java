package com.grsoft.dataobjects;

public class FirmEx extends Firm {
	public String prefix;
	public String suplName;
	public String suplAddress;
	public String suplPhone;
	public String suplInn;
	public String suplBank;
	public int onLine;
	public int divCode;
	
	@Override
	public String toString() {
		return name;
	}
}
