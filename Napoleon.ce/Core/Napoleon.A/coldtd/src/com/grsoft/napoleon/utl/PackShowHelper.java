package com.grsoft.napoleon.utl;

import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PackShowHelper {
	public static void updateTotalSum(TextView tvTotalSum, long sum , int count, int countPack) {
		
		tvTotalSum.setVisibility(View.VISIBLE);
		String sumStr = Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		String str;

		int si = 0, ei = sumStr.length(), ii = -1, iie = 0;
		if( countPack != 0 || count != 0 ) {
			str = "";
			if( countPack != 0 )
				str += Integer.toString(countPack) + "  ÿש";
			if( count != 0 ) {
				if( str.length() > 0 ) str += ", ";
				
				str += Integer.toString(count) + "  רע";
			}
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
	
	public static void drawItemQty(int color, OrderItem item, TextView tvQty, PriceEx p) {
		
		int inPack = p.qtyInPack;
		if( inPack == 0 )
			inPack = Consts.QTY_SCALE;
		int qty = (int)((long)item.qty * Consts.QTY_SCALE / inPack);
		String qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + " " + p.packName;
		tvQty.setText(qtyText);
		tvQty.setGravity(Gravity.RIGHT);
		tvQty.setTextColor(color);
	}
}
