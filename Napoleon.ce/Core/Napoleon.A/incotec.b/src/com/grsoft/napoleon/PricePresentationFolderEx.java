package com.grsoft.napoleon;

import com.grsoft.dataobjects.PriceEx;

import android.graphics.Color;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PricePresentationFolderEx extends PricePresentationFolder {
	
	@Override
	protected void setText(View view, PresentationData pd) {
		super.setText(view, pd);
		TextView tv = (TextView) view.findViewById(R.id.presentaion_info);
		if(tv == null) {
			LinearLayout ll = (LinearLayout)view;
			View v = view.findViewById(R.id.tvPriceItems);
			int idx = ll.indexOfChild(v);
			tv = new TextView(view.getContext());
			tv.setTextColor(Color.BLACK);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			
			android.widget.LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			ll.addView(tv, idx + 1, lp);
		}
		
		PriceEx pe = (PriceEx) pi.getData();
		tv.setText(Html.fromHtml(pe.info));
	}
}
