package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;

public class PriceCountEx extends PriceCount {

	@Override
	protected String getItemName(Price p) {
		return p.name + "\n" + ((PriceEx)p).remark.replaceAll("[\n\r]", "");
	}
}
