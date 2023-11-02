package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceEx extends PricePrint {
	@Scale(value=Consts.QTY_SCALE)
	public int whQty;
	
	public String barcode = "";
	
	public int dscType;

	@Scale(value=Consts.SUM_SCALE)
	public int minDsc;

	@Scale(value=Consts.SUM_SCALE)
	public int maxDsc;
	
	public String getDiscountText() {
		String ret = "";
		
		if(minDsc != 0 || maxDsc != 0) {
			if(minDsc == maxDsc || dscType == OrderEx.AUTO_DISCOUNT)
				ret = Util.IntToScaleStr(minDsc, Consts.SUM_SCALE) + " %";
			else
				ret = Util.IntToScaleStr(minDsc, Consts.SUM_SCALE) + " % - " + Util.IntToScaleStr(maxDsc, Consts.SUM_SCALE)+" %";
		}
		
		return ret;
	}
	
	public String getDscTypeText() {
		return dscType == OrderEx.AUTO_DISCOUNT ? "авто" :
			dscType == OrderEx.MANUAL_DISCOUNT ?  "ручная" :
			""; 
	}
}
