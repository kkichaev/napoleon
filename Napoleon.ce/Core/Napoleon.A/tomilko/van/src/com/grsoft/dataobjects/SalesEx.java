package com.grsoft.dataobjects;

public class SalesEx extends Sales {
	public String costCode;
	public String dogovor;
	public String ido;
	
	public int sum() {
		int sum = 0;
		for(OrderItem i : items)
			sum += ((SalesItem)i).sum;
		
		return sum;
	}
}
