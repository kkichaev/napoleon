package com.grsoft.napoleon;

import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocListEx extends DocList {
	
	@Override
	protected void refreshTotalSum(boolean useFilter) {
		if(DocType.getCurDoc() instanceof OrderDoc) {
			int sum = 0;
			int count = 0;
			PriceImpl pi = new PriceImpl();
			Price p = pi.getData();

			for( int i=0; i<adapter.getCount(); i++ ) {
				OrderImpl d = (OrderImpl) adapter.getItem(i);
				sum += getDocSum(d);
				
				for(OrderItem oi : d.getData().items) {
					p.id = oi.id;
					pi.read();
					if( p.qtyInPack != 0 )
						count += (int)((long)oi.qty * Consts.QTY_SCALE  / p.qtyInPack);
				}
			}
			
			pi.close();
			
			TextView tvTotalSum = (TextView) findViewById(R.id.tvDocSum);
			tvTotalSum.setVisibility(View.VISIBLE);
			String sumStr = Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			String str;
			int si = 0, ei = sumStr.length(), ii = -1, iie = 0;
			if( count != 0 ) {
				str = "";
				if( count != 0 )
					str += Util.IntToScaleStr(count, Consts.QTY_SCALE) + " ó";
				ii = 0;
				iie = str.length();
				ei += iie + 1;
				si += iie + 1;
				str += "\n" + sumStr;				
			} else
				str = sumStr;
			
			SpannableString ss = new SpannableString(str);
			if( ii >= 0 ) 
				ss.setSpan( new StyleSpan(android.graphics.Typeface.ITALIC), ii, iie, 0);			
			ss.setSpan( new StyleSpan(android.graphics.Typeface.BOLD), si, ei, 0);			
			tvTotalSum.setText(ss);//

			return;			
		} else
			super.refreshTotalSum(useFilter);
	}
}
