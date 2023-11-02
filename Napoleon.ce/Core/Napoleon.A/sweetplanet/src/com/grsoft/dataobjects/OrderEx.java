package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order {
	public int dovmoney = 0;
	
	@Scale(value=Consts.SUM_SCALE)
	public int money = 0;
	
	public int dovozv = 0;
	public int reesrt = 0;
}
