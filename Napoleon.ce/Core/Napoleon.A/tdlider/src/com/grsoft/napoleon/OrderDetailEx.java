package com.grsoft.napoleon;

import android.content.res.Configuration;
import android.widget.TextView;

public class OrderDetailEx extends OrderDetail {
	TextView tvTotalCount;
	
	protected void setContentView(){
		setContentView(R.layout.orderdetailex);
		
		tvTotalCount = (TextView) findViewById(R.id.tvTotalCount);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	protected void updateTotalSum() {
		super.updateTotalSum();
		
		tvTotalCount.setText(getString(R.string.lines_count, doc.getData().items.size()));
	}
}
