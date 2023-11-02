package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Return;
import com.grsoft.napoleon.documents.CreatableDocument;

public class ReturnImpl extends ReturnImplBase<Return> {

	@Override public CreatableDocument<Return> createInstance() { 
		return new ReturnImpl(); 
	}
}
