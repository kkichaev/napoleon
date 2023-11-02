package com.grsoft.dataobjects;

import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.impl.FirmImpl;
import com.grsoft.types.FieldOrder;

public class SalesItemEx extends SalesItem{
	
	@FieldOrder(order=USER_FIELDS)
	public String costCode = "";
	
	@FieldOrder(order=USER_FIELDS + 1)
	public int costIndex = 0;
	
	Boolean useTax = null;
	
	@Override
	public void countTax(Sales data, int tax) {
		if( useTax == null ) {
			FirmImpl fi = new FirmImpl();
			FirmEx f = (FirmEx)fi.getData();
			f.id = data.supplyercode;
			fi.read();
			fi.close();
			
			useTax = (f.tax > 0);
		}
		
		super.countTax(data, (useTax) ? tax : 0);
	}
}
