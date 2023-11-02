package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.impl.FirmImpl;

public class SalesPrintEx extends SalesPrint {
	Boolean useTax = null;

	public SalesPrintEx(Sales sales) {
		super(sales);
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
			
			useTax = (f.haveTax > 0);
		}
		if( !useTax )
			ret.itax = 0;
		return ret;
	}

}
