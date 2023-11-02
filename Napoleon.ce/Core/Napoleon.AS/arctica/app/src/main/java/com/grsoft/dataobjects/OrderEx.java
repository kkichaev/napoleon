package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order {
	@Scale(value=Consts.SUM_SCALE) 
	public int pkoCost;
	
	public int byPhone = 0;
	
	public String agreement = "";
	public String kagent = "";
}
