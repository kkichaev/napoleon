package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


public class SalesItemPrintEx extends SalesItemPrint {
	public String gost;
	public String cert;
	public String stdcond;
	public String bestBfr;
	public String article;
	
	public String date;

	public String barcode;
	public String barcodetype;

	String bc, bcPack;

	@Scale(value=Consts.QTY_SCALE, hideRest=true)
	public int itemCount;

	public SalesItemPrintEx(SalesItem item2, int index, int costType) {
		super(item2, index, costType);

		if(item2.inPack() && bcPack.length() > 0) {
			barcode = bcPack;
		} else {
			barcode = bc;
		}
	}
	
	@Override
	protected void init(Price p) {
		PriceEx pp = (PriceEx) p;
		gost = pp.gost;
		cert = pp.cert;
		stdcond = pp.stdcond;
		bestBfr = pp.bestBfr;
		article = pp.article;
		
		if(item.taxSum == 0)
			tax = "Áåç ÍÄÑ";
		
		int qip = pp.qtyInPack;
		if(qip == 0)
			qip = Consts.QTY_SCALE;
		
		itemCount = item.qty;

		bc = pp.barcode;
		bcPack = pp.barcodePack;
		if(bcPack.length() == 0)
			bcPack = bc;
		barcodetype = pp.barcodeType;
		if(barcodetype.length() == 0)
			barcodetype = "CODE128";
	}
}
