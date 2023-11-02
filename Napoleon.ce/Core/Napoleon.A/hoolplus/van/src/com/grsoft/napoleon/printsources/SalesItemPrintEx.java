package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.PricePrint;
import com.grsoft.dataobjects.PricePrintEx;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.util.Util;


public class SalesItemPrintEx extends SalesItemPrint {
	public String d1,d2,f1,f2,f3,f4,godn,mp,tu,sert;
	
	public SalesItemPrintEx(SalesItem item2, int index, int costType) {
		super(item2, index, costType);
	}
	
	@Override
	protected void init(PricePrint p) {
		PricePrintEx pp = (PricePrintEx) p;
		d1 = Util.simpleDateFormat.format(pp.d1);
		d2 = Util.simpleDateFormat.format(pp.d2);
		f1 = pp.f1;
		f2 = pp.f2;
		f3 = pp.f3;
		f4 = pp.f4;
		godn = pp.godn;
		mp = pp.mp;
		tu = pp.tu;
		sert = pp.sert; 
	}
}
