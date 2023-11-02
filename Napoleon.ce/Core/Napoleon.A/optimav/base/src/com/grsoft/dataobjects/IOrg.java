package com.grsoft.dataobjects;

public interface IOrg {
	int getCredit();
	boolean isApplyCreditLimit();
	int getPeriod();
	boolean isApplyPeriodLimit();
	String getId();
}
