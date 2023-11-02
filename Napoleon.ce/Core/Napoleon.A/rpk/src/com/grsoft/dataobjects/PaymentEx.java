package com.grsoft.dataobjects;

import com.grsoft.types.Scale;

public class PaymentEx extends Payment {
	@Scale(value=100)
	public int outSum;

	@Scale(value=1)
	public int color;
	
}
