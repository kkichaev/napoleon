package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.util.Consts;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.DocFilterOnClickListenerEx;
import com.grsoft.util.Util;
import android.os.Bundle;
import android.widget.TextView;

public class DocumentsEx extends Documents {
	protected DocFilterOnClickListener createDocFilter() {
		return new DocFilterOnClickListenerEx(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		((TextView)findViewById(R.id.tvOrgDebt)).setText(getString(R.string.org_debt, Util.IntToScaleStr(((OrgEx)org.getData()).debt, Consts.SUM_SCALE)));
	}
	
	@Override protected int getContentViewID() { return R.layout.documentsex; }
}
