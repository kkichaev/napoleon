package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
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

	@Override protected int getContentViewId() { return R.layout.pricecountex; }

	@Override
	protected void setItemImage(String fileName) {
		CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
		if(cfg.imagePosInPriceCount == 1) {
			cfg.imagePosInPriceCount = 0;
		}
		super.setItemImage(fileName);
	}

	@Override
	protected void refreshData() {
		super.refreshData();

		TextView tv = findViewById(R.id.tvInfo);
		PriceEx pe = (PriceEx) price.getData();
		tv.setText(Html.fromHtml(pe.info));
	}

	@Override
	protected void updateCost() {
		Price p = price.getData();
		long cost = getInputCost(p);
		cost = (cost * p.qtyInPack / Consts.QTY_SCALE);
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
