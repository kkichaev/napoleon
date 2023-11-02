package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((CheckBox) findViewById(R.id.cbVisit)).setChecked(true);
		findViewById(R.id.cbRemains).setVisibility(View.GONE);
	}
}
