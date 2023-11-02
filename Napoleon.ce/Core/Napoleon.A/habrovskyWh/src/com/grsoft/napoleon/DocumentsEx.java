package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class DocumentsEx extends Documents {
	private ImageButton btnSync;
	private ImageButton btnSetting;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnSync = (ImageButton) findViewById(R.id.btnSync);
		btnSetting =(ImageButton) findViewById(R.id.btnSetting);
		
		btnSync.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 UpdateDB.open(v.getContext());
			}
		});
		
		btnSetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Setting.open(v.getContext());
			}
		});
		
		unregisterForContextMenu(lvDocs);
	}
	@Override
	protected int getContentViewID() {
		return R.layout.documentsex;
		
	}
}
