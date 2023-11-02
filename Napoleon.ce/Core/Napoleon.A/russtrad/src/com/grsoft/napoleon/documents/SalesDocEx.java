package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.SalesImplEx;

public class SalesDocEx extends SalesDoc {
	
	public static void init() {
		instance = new SalesDocEx();
	}

	SalesDocEx() {
		super(SalesImplEx.class);
	}
}
