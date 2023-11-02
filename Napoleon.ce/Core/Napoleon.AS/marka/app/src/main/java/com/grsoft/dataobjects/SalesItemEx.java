package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.FieldVersion;

public class SalesItemEx extends SalesItem {
	
	@FieldVersion(version=1)
	public List<PriceSalesQty> party = new ArrayList<PriceSalesQty>();

	@Override
	public void countTax(Sales owner, int tax) {
//		if(((SalesEx)owner).useTax == 0) {
//			tax = 0;
//		}
		super.countTax(owner, tax);
	}

	public int partyQty() {
		int vq = 0;
		for(PriceSalesQty psq : party)
			vq += psq.qty;
		
		return vq;
	}
}
