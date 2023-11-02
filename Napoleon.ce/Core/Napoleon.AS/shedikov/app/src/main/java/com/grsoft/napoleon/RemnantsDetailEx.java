package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;


public class RemnantsDetailEx extends RemnantsDetail {
	View btnEd;
	
	@Override protected int getLayoutId() { return R.layout.remnantsdetailex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btnEd = findViewById(R.id.btnEditOrder);
		
		btnEd.setOnClickListener(editClick());
	}

	private OnClickListener editClick() {
		return new OnClickListener() { @Override public void onClick(View v) { CreateRemnants.openEdit(v.getContext(), remnantsImpl); }};
	}
	
	@Override
	protected void removeEmptyDoc() { }
	
}
