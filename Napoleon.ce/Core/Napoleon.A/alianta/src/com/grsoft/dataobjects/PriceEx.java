package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	public static final int FL_CAN_ACTION = 8;
		
	public int flags;
	
	@Scale(value = Consts.QTY_SCALE)
	public int rezerv = 0;
	
	public String info = "";
	
	public String region = "";
	public String country = "";
	public String grape = "";

	public boolean inAction() { return ((flags & FL_CAN_ACTION) != 0); }
}
