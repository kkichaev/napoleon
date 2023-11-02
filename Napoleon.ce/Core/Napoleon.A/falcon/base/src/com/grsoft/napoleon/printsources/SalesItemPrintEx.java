package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.dataobjects.SalesItem;

public class SalesItemPrintEx extends SalesItemPrint {

	public String barcode = "";
	
	public SalesItemPrintEx(SalesItem item2, int index, int costType) {
		super(item2, index, costType);
	}

	@Override protected void init(PricePrint pp) {
		barcode = ((PriceEx)pp).barcode;
		
	}
}
