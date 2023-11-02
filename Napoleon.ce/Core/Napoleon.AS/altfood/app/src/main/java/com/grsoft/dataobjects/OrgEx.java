package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	@Scale(value=Consts.SUM_SCALE)
	public int due;
	
	@Scale(value=Consts.SUM_SCALE)
	public int postdue;
	
	@Scale(value=Consts.SUM_SCALE)
	public int minSum;
	
	public String priceLevel = ""; 
	
	public List<OrgDog> dogovors = new ArrayList<OrgDog>();

}
