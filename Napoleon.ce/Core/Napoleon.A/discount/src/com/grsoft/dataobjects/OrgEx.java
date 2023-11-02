package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org {
	public String orgType = "";
	
	public List<OrgCostTypes> whPriceTypes = new ArrayList<OrgCostTypes>();
	public List<ExistMatrix> matrixExist = new ArrayList<ExistMatrix>();
}
