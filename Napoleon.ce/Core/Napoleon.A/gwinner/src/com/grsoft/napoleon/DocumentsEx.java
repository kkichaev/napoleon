package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.util.OrgInfoClickListener;
import com.grsoft.util.ExtrasConst;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TabHost;
import android.widget.TextView;

public class DocumentsEx extends TabActivity {
	protected static final String ONLY_VISIT = "only_visit";
	protected static final String DOCS_TAG = "Документы";
	protected static final String INFO_TAG = "Информация";

	OrgImpl org = new OrgImpl();
	
	boolean onlyVisit;
	String orgId = "";
	
	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		
		setContentView(R.layout.documentsex);
		
		if( b == null )
			b = getIntent().getExtras();
		
		onlyVisit = b.getBoolean(ONLY_VISIT, false);
		orgId = b.getString(ExtrasConst.ORG_ID_STR);
		
		org = new OrgImpl();
		org.getData().id = orgId;
		org.read();
		org.close();
		
		
		TextView tv = (TextView)findViewById(R.id.tvOrgInfo);
		tv.setText(Html.fromHtml(orgInfo(org.getData())));
		
		findViewById(R.id.llHeader).setOnClickListener(new OrgInfoClickListener(org.getData(), R.layout.org_detail_info_row, null));
		
		TabHost th = getTabHost();
		
		Intent i = new Intent();
		i.setClass(this, DocChild.class);
		i.putExtra(ONLY_VISIT, onlyVisit);
		i.putExtra(ExtrasConst.ORG_ID_STR, orgId);
		
		TabHost.TabSpec ts = th.newTabSpec(DOCS_TAG);
		ts.setIndicator("Документы");
		ts.setContent(i);		
		th.addTab(ts);
		
		i = new Intent();
		i.setClass(this, OrgInfo.class);
		i.putExtra(ExtrasConst.ORG_ID_STR, orgId);
		
		ts = th.newTabSpec(INFO_TAG);
		ts.setIndicator("Информация");
		ts.setContent(i);
		th.addTab(ts);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putBoolean(ONLY_VISIT, onlyVisit);
		outState.putString(ExtrasConst.ORG_ID_STR, orgId);
	}

	protected String orgInfo(Org o) {
		String ret = o.name;
		if(Features.SHOW_ORG_ADDRESS && o.address.length() > 0 ) {
			ret += "<br><i>" + o.address + "</i>";
		}
		return ret; 
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		org.close();
	}
}
