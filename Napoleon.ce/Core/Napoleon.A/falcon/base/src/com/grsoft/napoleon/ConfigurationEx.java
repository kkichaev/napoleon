package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ConfigurationEx extends Configuration {
	@Override
	protected int getLayoutID() {
		return R.layout.configex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnSync).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UpdateDBEx.openActivity(v.getContext());
			}
		});
	}
}
