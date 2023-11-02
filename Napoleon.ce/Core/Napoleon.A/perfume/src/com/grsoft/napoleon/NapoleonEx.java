package com.grsoft.napoleon;

import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.DocFilterOnClickListenerEx;

public class NapoleonEx extends Napoleon {
	
	@Override
	protected DocFilterOnClickListener createDocFilter() {
		return new DocFilterOnClickListenerEx(this);
	}
}
