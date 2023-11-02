package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	@Scale(value=Consts.SUM_SCALE)
	public int balance;
	
	public int outDays;
	public List<OrgPriceItem> price = new ArrayList<OrgPriceItem>();

}
