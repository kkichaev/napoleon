package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	@Scale(value=Consts.SUM_SCALE)
	public int balance;

	@Scale(value=Consts.SUM_SCALE)
	public int limit;
	
	public int delay;
	
	public String prcType;
	
	public List<OrgDiscount> discounts = new ArrayList<OrgDiscount>(); 
}
