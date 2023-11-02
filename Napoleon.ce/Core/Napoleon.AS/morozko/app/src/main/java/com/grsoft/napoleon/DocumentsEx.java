package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.TextView;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {
	@Override
	protected int getContentViewID() {
		return R.layout.documentsex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView debtSum = (TextView) findViewById(R.id.tvDebtSum);
		debtSum.setText(Util.IntToScaleStr(((OrgEx)org.getData()).debt, Consts.SUM_SCALE, Util.DEC_DELIM, false));
	}
}
