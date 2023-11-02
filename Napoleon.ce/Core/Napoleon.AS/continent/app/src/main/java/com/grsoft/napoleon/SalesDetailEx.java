package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;

public class SalesDetailEx extends SalesDetail {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.btnPrint).setVisibility(View.GONE);
	}
}
