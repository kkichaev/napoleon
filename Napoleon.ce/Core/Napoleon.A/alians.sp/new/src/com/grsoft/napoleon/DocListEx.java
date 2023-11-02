package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.TextView;


public class DocListEx extends DocList {
	private TextView tvDocCnt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tvDocCnt = (TextView) findViewById(R.id.tvDocCnt);
	}
	
	@Override protected int getViewID() {	return R.layout.doclistex; }
	
	@Override
	protected void refreshTotalSum(boolean useFilter) {
		super.refreshTotalSum(useFilter);
		
		if(tvDocCnt != null)
			tvDocCnt.setText(Integer.toString(adapter.getCount()));
	}
}
