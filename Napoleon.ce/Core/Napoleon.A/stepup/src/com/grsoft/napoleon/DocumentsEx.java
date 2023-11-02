package com.grsoft.napoleon;

import android.view.View;


public class DocumentsEx extends Documents {
	@Override
	protected void onResume() {
		super.onResume();
		findViewById(R.id.tvTotalSum).setVisibility(View.GONE);
	}
}
