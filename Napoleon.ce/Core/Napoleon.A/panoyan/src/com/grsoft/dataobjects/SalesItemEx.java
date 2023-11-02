package com.grsoft.dataobjects;

public class SalesItemEx extends SalesItem {
	@Override
	public void countTax(Sales owner, int tax) {
		if(((SalesEx)owner).useTax == 0) {
			tax = 0;
		}
		super.countTax(owner, tax);
	}
}
