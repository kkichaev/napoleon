package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.impl.FirmImpl;

public class SalesPrintEx extends SalesPrint {
	
	Boolean useTax = null;

	public String legalAddress;

	public SalesPrintEx(Sales sales) {
		super(sales);
	}
	
	@Override
	protected void initSupplyer(Sales sales) {
		supplSource = new SupplSourceEx();
		supplSource.setSupplyer(sales.supplyercode);
	}
	
	@Override
	protected SalesPrintItems createPrintItems() {
		return new SalesPrintItemsEx(this, getUseTax());
	}
	
	boolean getUseTax() {
		if( useTax == null ) {
			FirmImpl fi = new FirmImpl();
			FirmEx f = (FirmEx)fi.getData();
			f.id = sales.supplyercode;
			fi.read();
			fi.close();
			
			useTax = (f.tax > 0);
		}
		
		return useTax;
	}
	
	@Override
	protected SalesItemPrint createItemPrint(Sales sales, int index, OrderItem item) {
		SalesItemPrintEx ret = new SalesItemPrintEx((SalesItem)item, index, sales.sumType);

		if( !getUseTax() ) {
			ret.itax = 0;
			ret.taxT = "Без НДС";
			ret.tax = "Без НДС";
			ret.sumtax = "-"; 
		} else {
			ret.taxT = Integer.toString(ret.itax);
			ret.tax = ret.taxT;
		}
		
		return ret;
	}
}
