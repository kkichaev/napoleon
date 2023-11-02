package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	public List<OrgPrice> price = new ArrayList<OrgPrice>();
	
	@Scale(value=Consts.SUM_SCALE)
	public int debt = 0;
}
