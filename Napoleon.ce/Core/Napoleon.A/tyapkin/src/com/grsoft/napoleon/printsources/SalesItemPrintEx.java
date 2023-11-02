package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.SalesItem;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class SalesItemPrintEx extends SalesItemPrint {
	public String taxT;
	
	public SalesItemPrintEx(SalesItem item2, int index, int costType) {
		super(item2, index, costType);
		
		//work around cost == 0
		if( item2.costWOtax == 0 && item2.sum != 0 ) {
			item2.costWOtax = (int)(((long)item2.sum - item2.taxSum) * Consts.QTY_SCALE / item2.qty);
			cost = Util.IntToScaleStr(item2.costWOtax, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		}
	}

}
