package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.IncassImplEx;
import com.grsoft.napoleon.Features;

public class IncassDocEx extends IncassDoc {
	public static void init() {
		Features.INCASS_DEBET_DISTRIB = true;
		instance = new IncassDocEx(DOC_NAME, OBJ_NAME, IncassImplEx.class);
	}

	protected IncassDocEx(String docName, String objName,  Class<? extends Document<?>> docClass){
		super(docName, objName, docClass);
	}
	
	@Override public boolean outOfScript() { return false; }
}
