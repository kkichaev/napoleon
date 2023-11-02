package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	public int delay;
	
	@Scale(value=Consts.SUM_SCALE)
	public int discount;
	
	public List<OrgDiscount> fldDsc;
	
	public String ido;
	
	public List<DisabledItem> disabled = new ArrayList<DisabledItem>();
}
