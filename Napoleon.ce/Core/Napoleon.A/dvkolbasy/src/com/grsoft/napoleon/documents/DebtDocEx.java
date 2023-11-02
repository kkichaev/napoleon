package com.grsoft.napoleon.documents;

public class DebtDocEx extends DebtDoc {
	public static void init(){
		instance = new DebtDocEx();
	}
	
	@Override
	protected DebtDocList createDebtDocList(String where, String order, boolean LoadDelivery) {
		return new DebtDocListEx(where, order, LoadDelivery);
	}
}
