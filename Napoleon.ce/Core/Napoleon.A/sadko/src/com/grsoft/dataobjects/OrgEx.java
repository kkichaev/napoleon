package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org {
	public String stopMsg = "";
	public List<OrgMatrix> orgMatrix = new ArrayList<OrgMatrix>();
	public List<OrgDiscount> discounts = new ArrayList<OrgDiscount>();
	
	@Override public boolean isStopList() { return stopMsg.length() > 0; }
}
