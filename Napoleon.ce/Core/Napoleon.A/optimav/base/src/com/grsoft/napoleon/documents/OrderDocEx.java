package com.grsoft.napoleon.documents;

public class OrderDocEx extends OrderDoc{
	@Override
	public void refreshDocSum(String orgId) {
		super.refreshDocSum(orgId);
		DebtDoc.instance().refreshDocSum(orgId);
	}
}
