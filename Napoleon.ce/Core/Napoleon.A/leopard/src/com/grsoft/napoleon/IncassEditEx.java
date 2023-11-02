package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;

public class IncassEditEx extends IncassEdit {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.edRemark).setVisibility(View.INVISIBLE);
	}
}
