package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class DiscountObj extends DocDataObject {
	public String folder;
	
	public int level;
	
	@Scale(value=Consts.SUM_SCALE)
	public int discount;
}
