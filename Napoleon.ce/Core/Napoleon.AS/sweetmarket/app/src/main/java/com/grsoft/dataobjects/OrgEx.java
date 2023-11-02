package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org {
	public List<OrgDiscount> groupDiscount = new ArrayList<OrgDiscount>();
	public List<OrgCost> itemCost = new ArrayList<OrgCost>();

	@Scale(value = Consts.SUM_SCALE)
	public int income = 0;
	@Scale(value = Consts.SUM_SCALE)
	public int debet = 0;
	@Scale(value = Consts.SUM_SCALE)
	public int overdue = 0;
	@Scale(value = Consts.SUM_SCALE)
	public int limit = 0;
}
