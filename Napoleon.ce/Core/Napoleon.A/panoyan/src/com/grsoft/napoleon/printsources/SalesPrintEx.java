package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgPrint;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesItem;

public class SalesPrintEx extends SalesPrint {

	public String dogovor;
	
	public SalesPrintEx(Sales sales) {
		super(sales);
	}

	@Override
	protected void initOrg(OrgPrint op) {
		super.initOrg(op);
		
		OrgEx oe = (OrgEx)op;
		
		dogovor = oe.dogovor;
		if(oe.divName.length() > 0)
			name = oe.divName;
	}
	
	@Override
	protected SalesItemPrint createItemPrint(Sales sales, int index, OrderItem item) {
		return new SalesItemPrintEx((SalesItem)item, index, sales.sumType);
	}
	
	@Override
	protected SupplSource createSupplSource() {
		return new SupplSourceEx();
	}
}
