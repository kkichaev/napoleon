package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org {
	public List<OrgDiscount> groupDiscount = new ArrayList<OrgDiscount>();
	public List<OrgCost> itemCost = new ArrayList<OrgCost>();
}
