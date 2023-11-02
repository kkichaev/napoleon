package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class DocListEx extends DocList {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnSend.setVisibility(View.GONE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.getItem(OptionsMenuHelper.MNU_SEND_DOC_ID).setVisible(false);
		return result;
	}
}
