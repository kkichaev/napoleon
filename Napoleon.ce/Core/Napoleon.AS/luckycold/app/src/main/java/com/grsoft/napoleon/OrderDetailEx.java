package com.grsoft.napoleon;

import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class OrderDetailEx extends OrderDetail {
	@Override
	protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
		int qty = item.qty;
		String s = getString(R.string.oneitem);
		
		if(item.inPack()){
			Price p = price.getData();
			int inPack = p.qtyInPack;
			if( inPack == 0 )
				inPack = Consts.QTY_SCALE;
			
			
			qty = (int)((long)item.qty * Consts.QTY_SCALE / inPack);
			s = getString(R.string.packitem);
		}
		
		String text = String.format("%s %s", Util.IntToScaleStr(qty, Consts.QTY_SCALE, Util.DEC_DELIM,true), s);
	
		tvQty.setText(text);
		tvQty.setGravity(Gravity.RIGHT);
		tvQty.setTextColor(color);
	}
	
	@Override
	protected void updateTotalSum() {
		
		int packQty = 0;
		int qty = 0;
		long sum = doc.sum();
//		int weight = doc.weight();
		
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();
		
		for(OrderItem i : doc.getData().items) {
			if( i.inPack() ) {
				p.id = i.id;
				pi.read();
				
				int inPack = p.qtyInPack;
				if( inPack == 0 )
					inPack = Consts.QTY_SCALE;
				
				packQty += (int)((long)i.qty * Consts.QTY_SCALE / inPack);
			} else {
				qty += i.qty;
			}
		}
		
		pi.close();
		
		
		TextView tvTotalSum = (TextView) findViewById(R.id.tvTotalSum);		
		if (tvTotalSum != null) {
			tvTotalSum.setVisibility(View.VISIBLE);
			String sumStr = Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			String str;
			int si = 0, ei = sumStr.length(), ii = -1, iie = 0;
			if( packQty != 0 || qty != 0 ) {
				str = "";
				if( packQty != 0 )
					str += Integer.toString(packQty / Consts.QTY_SCALE) + " " + getString(R.string.packitem);
				if( qty != 0 ) {
					if( str.length() > 0 ) str += ", ";
					str += Integer.toString(qty / Consts.QTY_SCALE) + " " + getString(R.string.sht);
				}
//				if( weight != 0 ) {
//					if( str.length() > 0 ) str += ", ";
//					
//					str += weightToString(weight, getString(R.string.kg));
//				}
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
		}
	}
}
