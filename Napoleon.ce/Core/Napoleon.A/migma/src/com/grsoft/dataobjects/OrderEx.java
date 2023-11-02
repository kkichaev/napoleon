package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order {
	@Scale(value=Consts.DISCOUNT_SCALE)
	public int discount;
	
	public String costtype = "";

	public int whIndex;
	public String whCode;
}
