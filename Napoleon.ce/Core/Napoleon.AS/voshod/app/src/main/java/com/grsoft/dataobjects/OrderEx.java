package com.grsoft.dataobjects;

import java.util.Date;

public class OrderEx extends Order {
	public static final int OF_DELIVERY = 0x0008;
	
	public String dogovor = "";
	public String suplCode = "";
	public Date payDate = new Date();
}
