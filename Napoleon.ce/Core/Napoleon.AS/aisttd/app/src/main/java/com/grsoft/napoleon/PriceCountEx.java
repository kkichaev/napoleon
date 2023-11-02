package com.grsoft.napoleon;

import com.grsoft.dataobjects.PriceEx;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class PriceCountEx extends PriceCount {
	@Override protected int getContentViewId() { return R.layout.pricecountex; }

	@Override
	protected boolean getStartInPack() {
		PriceEx pe = ((PriceEx)price.getData());
		return pe.onePack == 1;
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		PriceEx pe = ((PriceEx)price.getData());
		cbPackets.setEnabled(pe.onePack != 1);
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvInfo);
		tv.setText(Html.fromHtml(pe.info));
		
		tv = (TextView)findViewById(R.id.tvBestBefore);
		String text = "";
		if(pe.bestBefore.length() > 0)
			text = pe.bestBefore;
		tv.setText(text);
	}
}
