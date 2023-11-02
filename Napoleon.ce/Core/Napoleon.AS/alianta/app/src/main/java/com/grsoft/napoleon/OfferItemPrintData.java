package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.dataobjects.OfferItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.util.Map;

public class OfferItemPrintData {
	public String info = "";

	public String id = "";

	public OfferItemPrintData(Context context, PriceEx pe, OfferItem oi) {
		info = "";

		if(pe.brand.length() > 0) {
//			info += "<b>" + context.getString(R.string.brand) + pe.brand + "</b><br/><br/>";
			info += context.getString(R.string.brand) + " " + pe.brand + "<br/><br/>";
		}
		info += pe.id + " " + pe.name;
		id = oi.id;

		boolean addedNewline = false;
		if(pe.region.length() > 0) {
			info += "<br/><br/>" + pe.region;
			addedNewline = true;
		}

		if(pe.strength > 0) {
			if(!addedNewline) {
				info += "<br/><br/>";
				addedNewline = true;
			} else {
				info += ", ";
			}
			String strength = Util.IntToScaleStr(pe.strength, 10);
			info += strength + " %";
		}

		if(oi.year > 0) {
			if(!addedNewline) {
				info += "<br/><br/>";
				addedNewline = true;
			} else {
				info += ", ";
			}
			info +=  context.getString(R.string.grape_year) + " " + Integer.toString(oi.year);
		}

//		if(addedNewline) {
//			info += "</font>";
//		}

//		if(pe.region.length() > 0)
//			info +="<br/><br/>" + pe.region;
		if(pe.grape.length() > 0)
			info += "<br/><br/>" + pe.grape;
		info += "<br/><br/><b>" + Util.IntToScaleStr(oi.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>";
	}
}
