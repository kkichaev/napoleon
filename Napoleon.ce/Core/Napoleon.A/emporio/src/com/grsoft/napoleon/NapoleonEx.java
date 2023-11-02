package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.napoleon.Napoleon;

public class NapoleonEx extends Napoleon {
	@Override
	public void init() {
		Org.clearCache();
		super.init();
	}
}
