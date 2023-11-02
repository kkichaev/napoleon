package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org {
	@Scale(value=Consts.SUM_SCALE)
	public int due;
	@Scale(value=Consts.SUM_SCALE)
	public int postdue;
	public String pers = "";

	public List<OrgFirmCost> costTypes = new ArrayList<>();

	@Scale(value=Consts.SUM_SCALE)
	public int limitsum;
	public int delay;
	public int back = 0;
}
