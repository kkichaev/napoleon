package com.grsoft.napoleon;

import com.grsoft.dataobjects.OfferItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OfferItemPrintData {
	public String info = "";

	public String id = "";
	
	public OfferItemPrintData(PriceEx pe, OfferItem oi) {
		info = pe.id + " " + pe.name;
		id = oi.id;
		if(pe.country.length() > 0)
			info += "<br/><br/>" + pe.country;
		if(pe.region.length() > 0)
			info +="<br/><br/>" + pe.region;
		if(pe.grape.length() > 0)
			info += "<br/><br/>" + pe.grape;
		info += "<br/><br/><b>" + Util.IntToScaleStr(oi.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>";
	}
}
