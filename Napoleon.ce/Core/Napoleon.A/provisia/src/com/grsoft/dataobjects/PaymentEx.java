package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.Scale;

public class PaymentEx extends Payment {
	
	public Date dlvDate;
	
	@Scale(value=1)
	public int delay; 

	public String type = "";
	
	@Scale(value=1)
	public int color;
}
