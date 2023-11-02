package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.Features;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class SalesItemPrint{
	public SalesItem item;
	public String id = "";
	public String sum;  
	public String qty;  
	public int iqty;
	public String qtyInPack;
	/**
	 * Кол-во мест
	 */
	@Scale(value=Consts.QTY_SCALE, hideRest=true)
	public int pack;
	public String name = "";
	public int num;
	public String cost;
	
	@Scale(value=Consts.SUM_SCALE, hideRest=false)
	public int costtax;
	public long isumwtax;
	public String sumwtax;
	public String sumtax;
	public String tax;
	public int itax;
	public String weight;
	public String brutto;
	public String unit;
	public String unitCode;	
	public String packName;
	public String country = "";
	public String countryCode = "";
	public String ntd = "";
	public long isum;
	public int isumtax;
	public int iweight;
	public int ibrutto;
	public String akz = "";
	public String icost = "";
	public String itemqty = "";
	public String traceCode = "-";
	public String traceName = "-";
	public String traceQty = "-";

	protected int itemCostWTax;
	
	public SalesItemPrint(SalesItem item2, int index, int costType){
		this.item = item2;
		this.num = index;
		
		PriceImpl priceImpl = new PriceImpl();
		priceImpl.getData().id = item2.id;
		
		priceImpl.read();
		priceImpl.close();
		
		Price pp = priceImpl.getData();
		
		name = pp.name;
		
		if( pp.unit.length() > 0 ) {
			unit = pp.unit;
			unitCode = (pp.unitCode.length() > 0) ? pp.unitCode : "796";			
		} else {
			unit = "шт";
			unitCode = "796";
		}
		
		iweight = (int)((long)pp.weight * item2.qty / Consts.QTY_SCALE);

		int qip = pp.qtyInPack;
		if( qip == 0 ) qip = Consts.QTY_SCALE;

		pack = (int)((long)item2.qty * Consts.QTY_SCALE / qip);
		if( pp.brutto != 0 ) {
			int restQty = item2.qty % qip;
			ibrutto = (int)(((long)pack * pp.brutto + (long)restQty * pp.weight)/ Consts.QTY_SCALE);
		}
		if( ibrutto == 0 )
			ibrutto = iweight;

//		if( (item2.qty % qip) != 0 )
//			pack++;
		
		weight = Util.IntToScaleStr(iweight, Consts.WEIGHT_SCALE);
		brutto = Util.IntToScaleStr(ibrutto, Consts.WEIGHT_SCALE);
		
		if( pp.packName.length() > 0 ) {
			packName = pp.packName;
		} else {
			packName = unit;
		}
				
		qtyInPack = Util.IntToScaleStr(qip, Consts.QTY_SCALE);
		country = pp.country;
		countryCode = pp.countryCode;
		
		if(pp.akciz == 0)
			akz = "без акциза";
		else
			akz = Util.IntToScaleStr(pp.akciz, Consts.SUM_SCALE);
		
		ntd = item2.ntd;
		if( country.length() == 0 )
			country = "-";
		if( countryCode.length() == 0 )
			countryCode = "-";
		if( ntd.length() == 0 )
			ntd = "-";
		
		itax = priceImpl.getData().tax1;
		if(item2.taxSum == 0)
			tax = "Без НДС";
		else
			tax = Integer.toString(itax) + " %";
		
		if(Features.USE_PACK_QTY_IN_FORMS && item2.inPack()) {
			if(pp.packCode.length() > 0 )
				unitCode = pp.packCode;
			if( pp.packName.length() > 0 )
				unit = pp.packName;

			
			iqty = (int)((long)item2.qty * Consts.QTY_SCALE / qip);
			itemCostWTax = (int)((long)item2.costWOtax * qip / Consts.QTY_SCALE );
			costtax = (int)((long)item2.cost * qip / Consts.QTY_SCALE );
//			cost = Util.IntToScaleStr((int)((long)item2.costWOtax * qip / Consts.QTY_SCALE ), Consts.SUM_SCALE, Util.DEC_DELIM, false);
			
		} else {
			iqty = item2.qty;
			itemCostWTax = item2.costWOtax;
			costtax = item2.cost;
//			cost = Util.IntToScaleStr(item2.costWOtax, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		}
		
		isum = item2.sum;
		isumtax = item2.taxSum;
		isumwtax = isum - isumtax;

		updateTextFields();
		
		id = item2.id;
		
		init(pp);
	}
	
	protected void updateTextFields() {
		icost = Util.IntToScaleStr(costtax, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		qty = Util.IntToScaleStr(iqty, Consts.QTY_SCALE);
		itemqty = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE);
		
		sum = Util.IntToScaleStr(isum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		sumtax = Util.IntToScaleStr(isumtax, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		sumwtax = Util.IntToScaleStr(isumwtax, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		
		// получим цену из суммы
		cost = Util.IntToScaleStr((int)((double)isumwtax / iqty * Consts.QTY_SCALE + 0.5) , Consts.SUM_SCALE, Util.DEC_DELIM, false);
	}

	protected void init(Price pp) {
	}
}
