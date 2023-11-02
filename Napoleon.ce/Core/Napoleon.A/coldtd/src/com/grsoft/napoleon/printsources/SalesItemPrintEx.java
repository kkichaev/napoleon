package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class SalesItemPrintEx extends SalesItemPrint {
	public String articul;
	public String packOKEI;
	public String packStr;
	boolean useArticle;
	
	@Scale(value=Consts.SUM_SCALE, hideRest=false)
	public int packCost;
	
	public SalesItemPrintEx(SalesItem item2, int index, int costType, boolean useArticle) {
		super(item2, index, costType);
		
		this.useArticle = useArticle;
		packStr = Util.IntToScaleStr(pack, Consts.QTY_SCALE);
		if( pack % Consts.QTY_SCALE != 0 ) {
			while( packStr.endsWith("0") )
				packStr = packStr.substring(0, packStr.length() - 1);
		}
		
		SalesItemEx src = (SalesItemEx)item2;
		ntd = src.ntd;
		country = src.country;
		countryCode = src.countryCode;
	}

	@Override
	protected void init(PricePrint pp) {
		super.init(pp);
		PriceEx pe = (PriceEx) pp;
		if( useArticle )
			articul = pe.articul;
		packOKEI = pe.packOKEI;
		
		if( pp.brutto != 0 ) {
			ibrutto = (int)((long)pp.brutto * iqty / Consts.QTY_SCALE);
			brutto = Util.IntToScaleStr(ibrutto, Consts.WEIGHT_SCALE);
		}
		
		int qip = pp.qtyInPack;
		if( qip == 0 ) qip = Consts.QTY_SCALE;
		pack = (int)((long)iqty * Consts.QTY_SCALE / qip);
		
		packCost = (int)((long)itemCostWTax * qip / Consts.QTY_SCALE );
	}
}
