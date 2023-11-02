package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.OrgPrint;
import com.grsoft.dataobjects.OrgPrintEx;
import com.grsoft.dataobjects.Sales;


public class SalesPrintEx extends SalesPrint {
	public SalesPrintEx(Sales sales) {
		super(sales);
	}

	@Override
	protected void initOrg(OrgPrint op) {
		super.initOrg(op);
		factAddress = ((OrgPrintEx) op).postadr;
		address = ((OrgPrintEx) op).legalAddress;
	}
}
