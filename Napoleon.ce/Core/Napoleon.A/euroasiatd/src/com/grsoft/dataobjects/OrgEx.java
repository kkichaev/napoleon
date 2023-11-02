package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org {
	public List<OrgDiscount> discounts = new ArrayList<OrgDiscount>();
	public List<OrgActionCost> actions = new ArrayList<OrgActionCost>();
	public List<OrgRetPrc> retPrc = new ArrayList<OrgRetPrc>();
	public List<MatrixItem> matrix = new ArrayList<MatrixItem>();
	public String dlvinfo = "";
	public int day = 0;
	public int day2 = 0;
}
