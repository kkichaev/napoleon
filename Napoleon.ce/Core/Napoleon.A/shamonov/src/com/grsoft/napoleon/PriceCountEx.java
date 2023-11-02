package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.util.Descr;

public class PriceCountEx extends PriceCount {
	
	@Override
	protected String getItemName(Price p) {
		String descr = Descr.read(this, p.id);
		
		if(descr.length() > 0)
			return p.name + "<br>" + descr;
		else 
			return super.getItemName(p);
	}
}
