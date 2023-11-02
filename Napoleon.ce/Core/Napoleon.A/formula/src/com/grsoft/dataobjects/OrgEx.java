package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	public List<OrgDiscountItem> discount = new ArrayList<OrgDiscountItem>();
	public List<OrgPriceItem> price = new ArrayList<OrgPriceItem>();
	public List<OrgCosTypeItem> costypes = new ArrayList<OrgCosTypeItem>();
	
	@Scale(value = Consts.SUM_SCALE)
 	public int disc;
}
