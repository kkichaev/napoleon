package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.SalesBan;

public class SalesBanImpl extends DbObject<SalesBan> {
	public static boolean isOrgBanned(String orgid) {
		SalesBanImpl sb = new SalesBanImpl();
		return sb.read("id", orgid) && sb.getData().value == 1; 
	}
}
