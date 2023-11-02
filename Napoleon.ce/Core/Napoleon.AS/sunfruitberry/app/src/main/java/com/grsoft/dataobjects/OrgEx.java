package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org {
	@Scale(value=Consts.SUM_SCALE)
	public int balance = 0;
	
	public String ido = "";
	public String email = "";

	public List<OrgCost> cost = new ArrayList<>();
}
