package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org{
	public String paytype = "";
	public String category = "";

	@Scale(value = Consts.SUM_SCALE)
	public int discount = 0;
	
	public List<OrgFolderDiscount> fldDsc = new ArrayList<OrgFolderDiscount>();
	public List<OrgPriceCost> prcCost = new ArrayList<OrgPriceCost>();
}
