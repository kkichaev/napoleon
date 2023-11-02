package com.grsoft.napoleon;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import com.grsoft.dataobjects.OrderEx;


public class OrderHelper {
	public static void setDriverView(View view, OrderEx o){
		TextView tv = (TextView) view.findViewById(R.id.tvRemark);
		tv.setVisibility(View.GONE);
		
		if(o.fio.length() > 0 || o.phone.length() > 0){
			
			tv.setVisibility(View.VISIBLE);
			tv.setMovementMethod(LinkMovementMethod.getInstance());
			tv.setText(Html.fromHtml(view.getContext().getString(R.string.remark_fmt, o.fio, o.phone)));
		}
	}
}
