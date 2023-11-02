package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgPrint;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.impl.FirmImpl;

public class SalesPrintEx extends SalesPrint {
	
	Boolean useTax = null;

	public String legalAddress;

	public SalesPrintEx(Sales sales) {
		super(sales);
	}

	@Override
	protected void initOrg(OrgPrint op) {
		super.initOrg(op);
		
		address = op.address;
		legalAddress = op.legalAddress;
	}
	
	@Override
	protected void initSupplyer(Sales sales) {
		supplSource = new SupplSourceEx();
		supplSource.setSupplyer(sales.supplyercode);
	}
	
	@Override
	protected SalesItemPrint createItemPrint(Sales sales, int index, OrderItem item) {
		SalesItemPrint ret = super.createItemPrint(sales, index, item); 

		if( useTax == null ) {
			FirmImpl fi = new FirmImpl();
			FirmEx f = (FirmEx)fi.getData();
			f.id = sales.supplyercode;
			fi.read();
			fi.close();
			
			useTax = (f.tax > 0);
		}
		if( !useTax )
			ret.itax = 0;
		return ret;
	}
}
