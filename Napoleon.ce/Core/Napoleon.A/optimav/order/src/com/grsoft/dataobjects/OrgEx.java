package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org implements IOrg{
	@Scale(value = Consts.SUM_SCALE)
	public int credit = 0;
	public int applyCreditLimit = 0;
	public int period = 0;
	public int applyPeriodLimit = 0;
	
	@Override
	public int getCredit() {
		return credit;
	}
	@Override
	public boolean isApplyCreditLimit() {
		return applyCreditLimit > 0;
	}
	@Override
	public int getPeriod() {
		return period;
	}
	@Override
	public boolean isApplyPeriodLimit() {
		return applyPeriodLimit > 0;
	}
	@Override
	public String getId() {
		return id;
	}
}
