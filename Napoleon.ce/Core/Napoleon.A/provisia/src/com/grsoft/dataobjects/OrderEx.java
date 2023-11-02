package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order {
	
	public static int ofNetCost = 2; // сетевая цена
	
	@Scale(value=Consts.DISCOUNT_SCALE)
	public int discount; // скидка на заявку

	public int sendBefore;
	
	public String supplCode;
	
	public int timeZone;	
}
