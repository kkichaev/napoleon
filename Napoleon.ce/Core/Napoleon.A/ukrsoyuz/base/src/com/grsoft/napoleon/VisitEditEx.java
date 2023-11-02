package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;

public class VisitEditEx extends VisitEdit {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnSend.setVisibility(View.GONE);
	}
}	