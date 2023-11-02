package com.grsoft.napoleon.documents;

import java.util.Collections;
import java.util.Comparator;

public class DebtDocEx extends DebtDoc {
	public static void init() {
		instance = new DebtDocEx();
	}
	
	@Override
	protected String getOrgWhere(String orgId) {
		String ret = super.getOrgWhere(orgId);
		if(orgId != null && orgId.length() > 0) {
			ret += " and hidden = 0";
		}
		return ret;
	}
	
	@Override
	protected DebtDocList createDebtDocList(String where, String order, boolean LoadDelivery) {
		return new DebtDocListEx(where, order, LoadDelivery);
	}
	
	class DebtDocListEx extends DebtDocList {
		
		public DebtDocListEx(String where, String order, boolean LoadDelivery) {
			super(where, order, LoadDelivery);
		}
		
		@Override
		protected void orderDocuments() {
			Collections.sort(items, new Comparator<DebetItem>() {

				@Override
				public int compare(DebetItem arg0, DebetItem arg1) {
					if(arg0.isDelivery) {
						return arg1.isDelivery ? arg0.index - arg1.index : 1;
					}
					
					return (arg1.isDelivery) ? -1 : arg0.index - arg1.index;
				}
			});
		}
	}
}
