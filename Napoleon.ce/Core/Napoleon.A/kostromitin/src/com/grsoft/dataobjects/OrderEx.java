package com.grsoft.dataobjects;

public class OrderEx extends Order {
	public int useTax = 0;
	public int lukoil = 0;
	public int secondWH = 0;
	
	public int discount = 0;
	
	public boolean useSecondWH() {
		return false;
//		return (params & ParamState.ofCash) == 0 && useTax > 0 && lukoil == 0;
	}
}
