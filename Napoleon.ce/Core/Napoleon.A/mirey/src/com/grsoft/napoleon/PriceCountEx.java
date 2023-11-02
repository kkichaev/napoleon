package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;

public class PriceCountEx extends PriceCount {
	@Override
	protected String getItemName(Price p) {
		String name = super.getItemName(p);
		name += "<br/><i>" + p.id + "</i>";
		return name;
	}
}
