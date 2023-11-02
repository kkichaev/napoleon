package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

public class PriceCountEx extends PriceCount {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		cbPackets.setVisibility(View.INVISIBLE);
	}
	
	@Override
	protected void updateCost() {
		Price p = price.getData();
		int cost = getInputCost(p);
		cost = (int)((long)cost * p.qtyInPack / Consts.QTY_SCALE);
		String value = Util.IntToScaleStr(cost, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		TextView tv = (TextView)findViewById(R.id.tvPrice);
		if( canChangeCost() ) {
			SpannableString ss = new SpannableString(value);
			ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
			tv.setTextColor(Color.BLUE);
			tv.setText(ss);
		} else {
			tv.setText(value);
			tv.setTextColor(Color.BLACK);
		}
	}
}
