package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;

public class ReturnDetailEx extends ReturnDetail {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnSend.setVisibility(View.GONE);
	}
}
