package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Return;
import com.grsoft.napoleon.documents.CreatableDocument;

public class ReturnImplEx extends ReturnImplBaseEx<Return> {

	@Override
	public CreatableDocument<Return> createInstance() {
		return new ReturnImplEx();
	}
}
