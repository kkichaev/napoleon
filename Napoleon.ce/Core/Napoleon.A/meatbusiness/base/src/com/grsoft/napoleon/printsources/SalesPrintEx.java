package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;

public class SalesPrintEx extends SalesPrint {
	
	public String schFactNumber;
	
	public SalesPrintEx(Sales sales) {
		super(sales);
		schFactNumber = ((SalesEx)sales).schFactNumber;
	}

}
