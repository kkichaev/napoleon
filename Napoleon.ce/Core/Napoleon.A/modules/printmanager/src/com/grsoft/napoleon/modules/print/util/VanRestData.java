package com.grsoft.napoleon.modules.print.util;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class VanRestData {
	public String name;
	
	@Scale(value=Consts.QTY_SCALE)
	public int qty;
	
	@Scale(value=Consts.WEIGHT_SCALE)
	public int weight;
}