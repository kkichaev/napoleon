package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order {
	public String whCode;
	
	@Scale(value=Consts.DISCOUNT_SCALE)
	public int discount;
	
	public String payRemark;
	
	public int retail;
	
	public String payType = "Отсрочка";
}
