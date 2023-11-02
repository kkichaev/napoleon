package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order {
	public String discount = "";
	public String deliveryType = "";
	
	@Scale(value=Consts.SUM_SCALE)
	public int dscValue = 0;
}
