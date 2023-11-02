package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;

public class MainEx extends Main {
	@Override protected int getResourceID() { return R.layout.mainex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.btnPrice).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { Warehouse.open(MainEx.this); }
		});
	}
}
