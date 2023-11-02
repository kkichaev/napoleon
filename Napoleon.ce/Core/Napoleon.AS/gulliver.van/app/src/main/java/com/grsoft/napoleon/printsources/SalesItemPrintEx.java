package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class SalesItemPrintEx extends SalesItemPrint {
//	public int iqtypack = 0;
//	public String qtypack = "";
//	public String unitpack = "";
//	public String unitCodepack = "";
//	public String costpack = "";
	
	public SalesItemPrintEx(SalesItem item2, int index, int costType) {
		super(item2, index, costType);
		
		PriceImpl priceImpl = new PriceImpl();
		priceImpl.getData().id = item2.id;
		
		priceImpl.read();
		priceImpl.close();
		
		Price pp = priceImpl.getData();
		
		if(item2.inPack()){
			iqty = (item2.qty / pp.qtyInPack) * Consts.QTY_SCALE;
			qty = Util.IntToScaleStr(iqty, Consts.QTY_SCALE);
			unit = packName;
			unitCode = "";
			cost = Util.IntToScaleStr((int)((long)(item2.sum - item2.taxSum) * Consts.QTY_SCALE / 
					iqty), Consts.SUM_SCALE, Util.DEC_DELIM, false);
			icost = Util.IntToScaleStr((int)((long)(item2.sum) * Consts.QTY_SCALE / 
					iqty), Consts.SUM_SCALE, Util.DEC_DELIM, false);
		}else{
			packName = "";
		}
	}

}
