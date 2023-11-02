package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.TextView;

import com.grsoft.dataobjects.OrgEx;


public class DocumentsEx extends Documents {
	@Override
	protected int getContentViewID() {
		return R.layout.documentsex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView tv = (TextView) findViewById(R.id.tvText);
		tv.setText(((OrgEx)org.getData()).text);
	}
}
