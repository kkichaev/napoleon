package com.grsoft.napoleon;

import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.impl.DeliveryImplBase;
import com.grsoft.dataobjects.impl.DeliveryImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class DeliveryDetailEx extends DeliveryDetail {
	@Override
	public void updateTotalSum(long sum, int weight) {
		DeliveryImplEx dlvex = (DeliveryImplEx) delivery;
		TextView tvTotalSum = (TextView)findViewById(R.id.tvTotalSum);
		
		tvTotalSum.setVisibility(View.VISIBLE);
		String sumStr = Util.IntToScaleStr(delivery.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false);
		String str;

		int count = dlvex.count();
		int si = 0, ei = sumStr.length(), ii = -1, iie = 0;
		if( count != 0 ) {
			str = "";
			
			if( count != 0 ) {
				if( str.length() > 0 ) str += ", ";
				
				str += Integer.toString(count) + "  ящ";
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
	
	DeliveryImplBase<? extends Delivery> createDelivery() { return new DeliveryImplEx(); }
}
