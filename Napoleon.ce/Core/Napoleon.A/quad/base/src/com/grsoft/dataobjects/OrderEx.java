package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends OrderPrint {
	
	public static final int CLIENT_DISCOUNT = 2;
	public static final int AUTO_DISCOUNT = 0;
	public static final int MANUAL_DISCOUNT = 1;
	
	public int whCode;
	public String ordNumber;
	
	@Scale(value = Consts.SUM_SCALE)
	public int debet;
	public String whId;
}
