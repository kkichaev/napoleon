package com.grsoft.napoleon;

import android.content.Context;

public class NapoleonEx extends Napoleon {
	
	@Override
	protected MainOrgsAdapter getMainOrgAdapter() throws IllegalAccessException, InstantiationException {
		return new OrgsAdapter(this);
	}
	
	class OrgsAdapter extends MainOrgsAdapter {
		public OrgsAdapter(Context context) {
			super(context, "sortOrder");
		}
	}
}
