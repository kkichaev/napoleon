package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.PurchaseDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {
	@Override
	protected void adjustViewForDocType(DocType docType) {
		if(docType == PurchaseDoc.instance())
			PurchaseList.open(this, org.getRowid());
		else
			super.adjustViewForDocType(docType);
	}
	
	@Override protected int getContentViewID() {	return R.layout.documentsex; }
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		StringBuilder sb = new StringBuilder();
		OrgEx oe = (OrgEx)org.getData();
		sb.append(getString(R.string.status, oe.info)).append("<br>");
		sb.append(getString(R.string.delay, oe.delay)).append("<br>");
		sb.append(getString(R.string.krlimit, Util.IntToScaleStr(oe.limit, Consts.SUM_SCALE)));
		
		TextView tv = (TextView) findViewById(R.id.tvInfo);
		tv.setText(Html.fromHtml(sb.toString()));
	};
}
