package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.types.Scale;

public class OrgEx extends Org {

	public String dogovor;
	
	@Scale(value=10)
	public int discount;
	
	@Scale(value=1)
	public int payDelay;	
	
	public List<OrgRemnants> remnants;
}
