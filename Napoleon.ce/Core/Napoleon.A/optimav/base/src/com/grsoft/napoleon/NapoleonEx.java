package com.grsoft.napoleon;


public class NapoleonEx extends Napoleon{
	@Override
	protected void onResume() {
		super.onResume();
		DocumentsEx.currentDocType = null;
	}
}
