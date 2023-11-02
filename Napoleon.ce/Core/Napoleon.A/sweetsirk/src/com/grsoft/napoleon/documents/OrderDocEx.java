package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.R;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class OrderDocEx extends OrderDoc {
	
	public OrderDocEx(String string, String string2, Class<OrderImplEx> class1) {
		super(string, string2, class1);
	}

	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("DebtDoc уже создан!");
		instance = new OrderDocEx("Заявки", "Order", OrderImplEx.class);
	}
	
	public void updateTotalSum(Activity activity, long sum, int weight, int count, int textViewId, long sumdisc){
		TextView tvTotalSum = (TextView) activity.findViewById(textViewId);		
		if (tvTotalSum != null)
		{
			tvTotalSum.setVisibility(View.VISIBLE);
			String s = getTotalSumStr(activity, sum, weight, count, sumdisc);			
			tvTotalSum.setText(Html.fromHtml(s));
		}
	}
	
	public String getTotalSumStr(Activity activity, long sum, int weight, int count, long disc) {
		StringBuilder sb = new StringBuilder();
		
		if( weight != 0 || count != 0 ) {
			sb.append("<i>");
			
			if( count != 0 )
				sb.append(Integer.toString(count));
				sb.append(" ");
				sb.append(getCountText(activity));
			if( weight != 0 ) {
				if( sb.length() > 0 ) 
					sb.append(", ");
				
				sb.append(weightToString(weight, activity.getString(R.string.kg)));
			}
			
			sb.append("</i><br>");				
		}
		
		sb.append("<b>");
		sb.append(SumConverter.toString(disc));
		sb.append("</b>");
		sb.append("<br>");
		sb.append("<b>");
		sb.append(SumConverter.toString(sum));
		sb.append("</b>");
		
		return sb.toString();
	}
}
