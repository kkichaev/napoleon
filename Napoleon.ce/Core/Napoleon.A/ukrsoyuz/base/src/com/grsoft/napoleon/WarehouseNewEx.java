package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceItemColor;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.AssortmentMatrixAdapterEx;
import com.grsoft.util.Util;

public class WarehouseNewEx extends WarehouseNew {
	
	OrgImpl oi = new OrgImpl();
	
	@Override
	protected AssortmentMatrixAdapter createAssortementMatrixAdapter() {
		return new AssortmentMatrixAdapterEx(this, document.getId());
	}
	
	@Override
	protected int getDefaultColor(Price p) {
		if(document != null) {
			Integer clr = PriceItemColor.getColor(p.id);
			if(clr != null)
				return Util.GrServerColorToSystem(clr);
		}
		return super.getDefaultColor(p);
	}
}
