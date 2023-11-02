package com.grsoft.napoleon;

import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.DocFilterOnClickListenerEx;

public class DocumentsEx extends Documents {
	protected DocFilterOnClickListener createDocFilter() {
		return new DocFilterOnClickListenerEx(this);
	}
}
