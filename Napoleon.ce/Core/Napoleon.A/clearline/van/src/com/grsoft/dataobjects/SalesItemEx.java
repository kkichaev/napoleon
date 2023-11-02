package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.CostStrategyEx;
import com.grsoft.types.FieldOrder;
import com.grsoft.types.FieldVersion;
import com.grsoft.util.Consts;

public class SalesItemEx extends SalesItem {
	
	@FieldVersion(version=1)
	@FieldOrder(order=USER_FIELDS)
	public List<PriceSalesQty> party = new ArrayList<PriceSalesQty>();
	
	@FieldVersion(version=2)
	@FieldOrder(order=USER_FIELDS+1)
	public String uid = UUID.randomUUID().toString().replace("-", "");

	@Override
	public void countTax(Sales owner, int tax) {
		
		int taxType = ((SalesEx)owner).taxType;
		if( taxType == OrgEx.TAX_ABOVE) {
			PriceImpl pi = new PriceImpl();
			PricePrint pp = (PricePrint)pi.getData();
			pp.id = id;
			pi.read();
			pi.close();
			
			int ccost = ((CostStrategyEx)CostStrategy.defaultInstance).getCost(pp, owner);
			double val = ((double)(pp.tax1)) / 100.0;
			double dcost = ccost + ccost * val;
			
			sum = (int)(dcost * qty /Consts.QTY_SCALE + 0.5);
			taxSum = (int)(ccost * val * qty / Consts.QTY_SCALE + 0.5);
			
			costWOtax = sum - taxSum;
			cost = (int)(dcost + 0.5);
		} else {
			if(((SalesEx)owner).taxType == OrgEx.TAX_NONE)
				tax = 0;
			super.countTax(owner, tax);
		}
	}
	
	public int partyQty() {
		int vq = 0;
		for(PriceSalesQty psq : party)
			vq += psq.qty;
		
		return vq;
	}
}
