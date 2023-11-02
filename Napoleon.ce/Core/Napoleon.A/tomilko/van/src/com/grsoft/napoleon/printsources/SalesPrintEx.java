package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgPrint;
import com.grsoft.dataobjects.Sales;

public class SalesPrintEx extends SalesPrint {

	public String mainName;
	public String mainAddress;

	public SalesPrintEx(Sales sales) {
		super(sales);
	}

	@Override
	protected void initOrg(OrgPrint op) {
		super.initOrg(op);
		mainName = ((OrgEx)op).mainName;
		mainAddress = ((OrgEx)op).mainAddress;
	}
}
