package com.grsoft.dataobjects;

public class PaymentDebt extends PaymentEx implements Comparable<PaymentDebt> {
	public String name = "";
	
	public PaymentDebt() {}

	@Override
	public int compareTo(PaymentDebt o) {
		return name.compareTo(o.name);
	}	
}
