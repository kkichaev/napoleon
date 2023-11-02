package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org{
	public List<OrgFolderDiscount> fldDsc = new ArrayList<OrgFolderDiscount>();
	public List<OrgPriceCost> prcCost = new ArrayList<OrgPriceCost>();
	
	public int firm = 0;
}
