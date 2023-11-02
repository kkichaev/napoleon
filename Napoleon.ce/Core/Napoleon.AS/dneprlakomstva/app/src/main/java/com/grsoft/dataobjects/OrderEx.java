package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order {
	
	public static final String ORD_INCASS = "Order";
	
	@Scale(value=Consts.SUM_SCALE)
	public int incass;
	
	public int willPay;
	
	@Scale(value=Consts.SUM_SCALE)
	public int willSum;
		
	public int docStatus;
	public int dlvStatus;
	
	public String docMessage = "";
	public String ordNumber = "";
	public String incassNum = "";
}
