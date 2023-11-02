package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;

public class PkoInfoEx extends PkoInfo {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = findViewById(R.id.btnSend);
		v.setVisibility(View.GONE);
		
		v = findViewById(R.id.edNumber);
		v.setEnabled(false);
	}
}
