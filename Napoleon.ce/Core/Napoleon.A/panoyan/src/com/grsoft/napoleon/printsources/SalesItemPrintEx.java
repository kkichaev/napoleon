package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.PricePrintEx;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.impl.PriceImpl;


public class SalesItemPrintEx extends SalesItemPrint {
	public String gost = "";
	public String cert = "";
	public String stcond = "";
	public String bestBfr = "";
	public String barcode = "";
	public String barcodeType = "";
	
	public SalesItemPrintEx(SalesItem item2, int index, int costType) {
		super(item2, index, costType);
		
		PriceImpl p = new PriceImpl();
		p.read("id", item2.id);
		
		PricePrintEx pp = (PricePrintEx) p.getData();
		
		gost = pp.gost;
		cert = pp.cert;
		stcond = pp.stcond;
		bestBfr = pp.bestBfr;
		
		if(item2.inPack()) {
			unitCode = "778";
			unit = "упак";
		} else {
			unitCode = "796";
			unit = "шт";
		}
		packName = unit;
		
		barcode = pp.barcode;
		barcodeType = (pp.barcode.length() < 13) ? "EAN8" : "EAN13";//pp.barcodeType;
		
		if(item2.taxSum == 0)
			tax = "Без НДС";
		
//		if(item2.inPack()) {
//			String pn = unit;
//			unit = packName;
//			unitCode = pp.packCode;
//			int qip = Consts.QTY_SCALE * Consts.QTY_SCALE / pp.qtyInPack;
//			qtyInPack = Util.IntToScaleStr(qip, Consts.QTY_SCALE);
//			packName = pn;
//			
//			iqty = (int)((long)item2.qty * qip / Consts.QTY_SCALE);
//			itemCostWTax = (int)((long)item2.costWOtax * Consts.QTY_SCALE / qip );
//			costtax = (int)((long)item2.cost * Consts.QTY_SCALE / qip );
//			cost = Util.IntToScaleStr(itemCostWTax, Consts.SUM_SCALE, Util.DEC_DELIM, false);
//			
//			icost = Util.IntToScaleStr(costtax, Consts.SUM_SCALE, Util.DEC_DELIM, false);
//			qty = Util.IntToScaleStr(iqty, Consts.QTY_SCALE);
//			itemqty = Util.IntToScaleStr(iqty, Consts.QTY_SCALE);
//		} else {
//			packName = unit;
//			qtyInPack = "1";
//		}
			
	}
}
