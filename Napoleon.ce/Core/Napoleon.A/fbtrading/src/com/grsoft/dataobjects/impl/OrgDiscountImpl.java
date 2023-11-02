package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrgDiscount;
import com.grsoft.dataobjects.OrgDiscountItem;

public class OrgDiscountImpl extends DbObject<OrgDiscount> {
	
	public static int getDiscount(String idOrg, String idItem) {
		int dsc = 0;
		
		OrgDiscountImpl odi = new OrgDiscountImpl();
		OrgDiscount od = odi.getData();
		
		od.id = idOrg;
		if( odi.read() )
			for(OrgDiscountItem i : od.items) {
				if(i.id.equals(idItem)) {
					dsc = i.discount;
					break;
				}
			}
		
		odi.close();
		return dsc;
	}
}
