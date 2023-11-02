package com.grsoft.napoleon;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.widget.TextView;

import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocListEx extends DocList {
	protected void refreshTotalSum(boolean useFilter) {
		TextView tv = (TextView)findViewById(R.id.tvDocSum);
		if( tv != null ) {
			int sum = 0;
			if( useFilter ) {
				int weight = 0;
				int count = 0;
				
				for( int i=0; i<adapter.getCount(); i++ ) {
					Document<?> d = (Document<?>) adapter.getItem(i);
					sum += getDocSum(d);
					
					if (d instanceof OrderImplBase<?>)
					{
						weight += ((OrderImplBase<?>)d).weight();
						count += ((OrderImplBase<?>)d).count();
					}
				}

				if (count > 0 || weight > 0){
					StringBuilder sb = new StringBuilder(); 
					String sumStr = Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
					int si = 0, ei = sumStr.length(), ii = -1, iie = 0;
					
					if (count > 0)
						sb.append(Integer.toString(count) + " רע");
					
					if (weight > 0){
						if (sb.length() > 0)
							sb.append(", ");
						
						final int SCALE = 100;
						weight = (int)(((long)weight * SCALE)/ Consts.WEIGHT_SCALE);
						
						sb.append(Util.IntToScaleStr(weight / 10, SCALE, Util.DEC_DELIM, true)).append(" האכ");
						
						ii = 0;
						iie = sb.length();
						ei += iie + 1;
						si += iie + 1;
						sb.append("\n").append(sumStr);	
						
						
						SpannableString ss = new SpannableString(sb.toString());
						if( ii >= 0 ) 
							ss.setSpan( new StyleSpan(android.graphics.Typeface.ITALIC), ii, iie, 0);			
						ss.setSpan( new StyleSpan(android.graphics.Typeface.BOLD), si, ei, 0);
						
						tv.setTypeface(Typeface.DEFAULT);
						tv.setText(ss);
						return; //Óןס.... ))
					}
				}
				
			} else
				sum = OrgSumImpl.docSum(DocType.getCurDoc().getName());
			
			tv.setText(Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		}
	}
}
