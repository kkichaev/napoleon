package com.grsoft.napoleon.documents;

import android.app.Activity;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderDocEx extends OrderDoc {
	
	protected OrderDocEx() { super("Заявки", "Order", OrderImplEx.class);} 
	
	static public void initialize() {
		instance = new OrderDocEx();
	}
	
	public void updateTotalSum(Activity activity, int sum, int weight, int count, int textViewId){
		TextView tvTotalSum = (TextView) activity.findViewById(textViewId);		
		if (tvTotalSum != null)
		{
			tvTotalSum.setVisibility(View.VISIBLE);
			String sumStr = Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			String str;
			int si = 0, ei = sumStr.length(), ii = -1, iie = 0;
			if( weight != 0 || count != 0 ) {
				str = "";
				if( count != 0 )
					str += Integer.toString(count) + " шт";
				if( weight != 0 ) {
					if( str.length() > 0 ) str += ", ";
					int scale = 100;
					
					// переводим масштаб
					if( scale != Consts.WEIGHT_SCALE )
						weight = (int)(((long)weight * scale)/ Consts.WEIGHT_SCALE);
					
					str += Util.IntToScaleStr(weight / 10, scale, Util.DEC_DELIM, true) + " дал";
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
	}
}
