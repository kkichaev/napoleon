package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import com.grsoft.dataobjects.PriceEx;


public class PriceCountEx extends PriceCount{
	
	@Override
	protected int getContentViewId() {	return R.layout.pricecountex; }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String info = ((PriceEx)price.getData()).info.trim();
		
		if (info.length() > 0){
			TextView tvInfo = (TextView) findViewById(R.id.tvInfo);
			tvInfo.setText(Html.fromHtml(info));
			tvInfo.setVisibility(View.VISIBLE);
		}
	}
}
