package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.SalesItemPrintRest;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class SalesItemPrintEx extends SalesItemPrint {
	public String gost;
	public String cert;
	public String stcond;
	public String bestBfr;
	
	public String date;
	public String name_date;
	public String barcode;
	public String barcodetype;

	String bc, bcPack;
	
	@Scale(value=Consts.QTY_SCALE, hideRest=true)
	public int itemCount;

	int packSvCost, packSvQty, itemSvCost, itemSvQty;
	String packSvName, packSvCode, qipSave;
	public String itemSvName, itemSvCode;
	
	public SalesItemPrintEx(SalesItem item2, int index, int costType) {
		super(item2, index, costType);
	}
	
	@Override
	protected void init(Price p) {
		PriceEx pp = (PriceEx) p;
		gost = pp.gost;
		cert = pp.cert;
		stcond = pp.stcond;
		bestBfr = pp.bestBfr;

		bc = pp.barcode;
		bcPack = pp.barcodePack;
		if(bcPack.length() == 0)
			bcPack = bc;
		barcodetype = pp.barcodeType;
		if(barcodetype.length() == 0)
			barcodetype = "CODE128";

		if(item.taxSum == 0)
			tax = "Áåç ÍÄÑ";
		
		if(pp.packCode.length() > 0 )
			packSvCode = pp.packCode;
		if( pp.packName.length() > 0 )
			packSvName = pp.packName;
		else
			packSvName = unit;

		int qip = pp.qtyInPack;
		if(qip == 0)
			qip = Consts.QTY_SCALE;
		
		packSvQty = (int)((long)item.qty * Consts.QTY_SCALE / qip);
		packSvCost = (int)((long)item.cost * qip / Consts.QTY_SCALE );
		
		itemCount = item.qty;

		itemSvQty = item.qty;
		itemSvCost = item.cost;
		qipSave = qtyInPack;
		
		if( pp.unit.length() > 0 ) {
			itemSvName = pp.unit;
			itemSvCode = (pp.unitCode.length() > 0) ? pp.unitCode : "796";			
		} else {
			itemSvName = "øò";
			itemSvCode = "796";
		}
		
		packName = "";
		
		name_date = name;
		if(item instanceof SalesItemPrintRest) {
			date = Util.simpleDateFormat.format(((SalesItemPrintRest)item).date);
			name_date += "/" + date;
		} else {
			date = "";
		}
	}
	
	public void setInPack(boolean inPack) {
		if(inPack) {
			iqty = packSvQty;
			costtax = packSvCost;
			
			unitCode = packSvCode;
			unit = packSvName;
			qtyInPack = qipSave;
			barcode = bcPack;
		} else {
			iqty = itemSvQty;
			costtax = itemSvCost;

			unitCode = itemSvCode;
			unit = itemSvName;
			qtyInPack = "1";
			barcode = bc;
		}
		updateTextFields();
	}
}
