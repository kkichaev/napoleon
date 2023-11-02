package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	public List<OrgDiscountItem> discount = new ArrayList<OrgDiscountItem>();
	public List<OrgDiscountItem> price = new ArrayList<OrgDiscountItem>();
	public List<OrgNacenItem> nacen = new ArrayList<OrgNacenItem>();
	
	@Scale(value = Consts.SUM_SCALE)
 	public int nac;
}
