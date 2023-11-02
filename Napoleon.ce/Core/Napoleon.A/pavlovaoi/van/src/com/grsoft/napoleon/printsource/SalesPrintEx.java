package com.grsoft.napoleon.printsource;

import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.napoleon.printsources.PrintInfo;
import com.grsoft.napoleon.printsources.SalesPrint;
import com.grsoft.napoleon.printsources.SupplSource;


public class SalesPrintEx extends SalesPrint {
	@PrintInfo(name="Основание")
	public String dogovor = "";
	public SalesPrintEx(Sales sales) {
		super(sales);
		
		dogovor = ((SalesEx)sales).dogovor;
	}
	
	@Override protected SupplSource createSupplSource() { return new SupplSourceEx(); }

}
