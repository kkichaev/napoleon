package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;

public class MainEx extends Main {
	@Override protected int getResourceID() { return R.layout.napoleonex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.btnIncassList).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { IncassList.open(MainEx.this); }
		});
	}
}
