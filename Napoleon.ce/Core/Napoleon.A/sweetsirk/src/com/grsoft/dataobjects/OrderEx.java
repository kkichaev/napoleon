package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order {
	@Scale(value=Consts.SUM_SCALE)
	public int disc = 0;
	
	@Scale(value=Consts.SUM_SCALE)
	public int zsum = 0;
	
	public String task = "";
	
	public int whIndex;
	public String whCode;

}
