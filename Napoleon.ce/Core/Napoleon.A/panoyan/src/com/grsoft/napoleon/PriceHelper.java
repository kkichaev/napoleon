package com.grsoft.napoleon;

import com.grsoft.util.Consts;

public class PriceHelper {
	public static String getQtyText(long qty, int qtyInPack) {
		if(qtyInPack > 0) {
			int rest = (int) (Math.abs(qty) % qtyInPack) / Consts.QTY_SCALE;
			String text = qty == 0 ? "0" : Long.toString(qty / qtyInPack) + "ê " + 
					((rest != 0) ? Long.toString(rest) + "ø" :"");
			return text;
		}
		return null;
	}
}
