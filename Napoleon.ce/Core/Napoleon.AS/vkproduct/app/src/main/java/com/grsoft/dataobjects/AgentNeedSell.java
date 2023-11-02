package com.grsoft.dataobjects;

public class AgentNeedSell extends AgentGroupPlan {
	public boolean needMoreSell() {
		return needSell > 0 || weight > 0;
	}
}
