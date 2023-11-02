package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;

public class NapoleonEx extends Napoleon {

	@Override
	protected void onResume() {
		super.onResume();

		String inwokr = ((NapoleonApp) getApplication()).getInWork();

		if (inwokr.length() > 0) {
			Org org = new Org();
			org.id = inwokr;
			DocumentsEx.open(this, org);
		}
	}
}
