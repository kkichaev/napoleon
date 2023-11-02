package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	@Scale(value=Consts.SUM_SCALE)
	public int debt;
	
	public int delay;
	
	public String catCode = "";
	public String category = "";
	public String segment= "";
	
	
	public List<OrgPrcGroup> priceGroups = new ArrayList<OrgPrcGroup>();
}
