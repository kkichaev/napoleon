package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.cbPresent).setVisibility(View.GONE);
	}
}
