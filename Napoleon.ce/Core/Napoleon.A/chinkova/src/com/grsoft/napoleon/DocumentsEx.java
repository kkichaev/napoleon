package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {
	TextView tvDebt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tvDebt = (TextView)findViewById(R.id.tvDebt);
	}
	
	@Override
	protected String getNonBlockingMessage() {
		StringBuilder result = new StringBuilder();
		result.append(getString(R.string.debet_stop_msg, 
				Util.IntToScaleStr(((OrgEx)org.getData()).debt, Consts.SUM_SCALE)));
		result.append(super.getNonBlockingMessage());
		
		return result.toString();
	}
	
	protected int getContentViewID() { return R.layout.documentsex; }
	
	@Override
	protected void onResume() {
		super.onResume();
		
		OrgEx oe = (OrgEx) org.getData();
		if(oe.debt > 0){
			tvDebt.setVisibility(View.VISIBLE);
			tvDebt.setText(getString(R.string.debet_stop_msg, Util.IntToScaleStr(oe.debt, Consts.SUM_SCALE)));
		}else
			tvDebt.setVisibility(View.GONE);
		
	}
}
