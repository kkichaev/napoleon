package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;


public class NapoleonEx extends Napoleon {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.tvMainDocValColTitle).setVisibility(View.GONE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		tvTotalSum.setVisibility(View.GONE);
	}
}
