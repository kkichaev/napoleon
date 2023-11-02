package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
	protected String orgInfo(Org o) {
		String ret = super.orgInfo(o);
		OrgEx oe = (OrgEx) org.getData();
		if(oe.debt > 0)
			ret += " <b><font color='#8B0000'>" + getString(R.string.debet_stop_msg, Util.IntToScaleStr(oe.debt, Consts.SUM_SCALE)) + "</font></b>";
		return ret;
	}
}
