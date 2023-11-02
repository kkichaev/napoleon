package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.Sales;
import com.grsoft.napoleon.SalesDetailEx;

public class SalesPrintEx extends SalesPrint {
	boolean hasnds = false;
	
	public SalesPrintEx(Sales sales) {
		super(sales);
		
		if(sales != null){
			hasnds = SalesDetailEx.isNDSFirm(sales.supplyercode);
			((SalesPrintItemsEx)items).hasnds = hasnds;
		}
	}
	
	@Override
	protected SalesPrintItems createPrintItems() {
		return new SalesPrintItemsEx(this);
	}
	
	@Override
	public boolean getValue(StringBuilder value, String name) {
		if(!hasnds){
			if (name.equals("pagesumwtax"))
				return super.getValue(value, "pagesum");
			if (name.equals("pagesumtax") || name.equals("sumtax")){
				value.append("");
				return true;
			}else if (name.equals("sumwtax"))
				return super.getValue(value, "sum");
			else
				super.getValue(value, name);
		}
		return super.getValue(value, name);
	}
}
