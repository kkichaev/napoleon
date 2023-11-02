package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.util.Descr;

public class PricePresentationEx extends PricePresentation {
	@Override
	protected void postAppendPrice(StringBuilder sb, Price p) {
//		if(((PriceEx)p).descr != null && ((PriceEx)p).descr.length() > 0 )
//			sb.append(" - <b><i>" + ((PriceEx)p).descr + "</i></b>");
		String descr = Descr.read(this, p.id);
		
		if(descr.length() > 0){
			sb.append("<br>");
			sb.append(descr);
		}
	}
}
