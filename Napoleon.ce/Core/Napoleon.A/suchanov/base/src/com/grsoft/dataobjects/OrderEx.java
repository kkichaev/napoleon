package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order {
	public static final int TOPIC_B = 1;
	public static final int DISCOUNT = 2;
	public static final int TAX = 4;
	
	public int flags;
	public int bank;
	public Date pay;
	public int specCondition;
	
	@Scale(value=Consts.DISCOUNT_SCALE)
	public int discount;
}
