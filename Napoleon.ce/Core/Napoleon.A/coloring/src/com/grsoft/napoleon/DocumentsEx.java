package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.widget.TextView;

public class DocumentsEx extends Documents {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		OrgEx orgEx = (OrgEx) org.getData();
		TextView tvLicense = (TextView) findViewById(R.id.tvLicense);
		
		tvLicense.setText(getString(R.string.license_date, 
				orgEx.license.getTime() == -14400000 ? "..." : 
				Util.simpleDateFormat.format(orgEx.license)));
		
	}
	
	@Override
	protected int getContentViewID() {
		return R.layout.documentsex;
	}
}
