package com.grsoft.napoleon;

import com.grsoft.util.Consts;

public class QuantHelper {
	public int roundToQuant(int inQty, int inQuant) {
		int result = inQuant == 0 ? 1 : inQuant;
		
		double quant = ((double)result) / Consts.QTY_SCALE;
		double qty = ((double)inQty) / Consts.QTY_SCALE;
		
		result = (int) (Math.round(qty/quant) * quant * Consts.QTY_SCALE); 
		
		return result;
	}
}
