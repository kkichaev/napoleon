package com.grsoft.napoleon.documents;


public class VisitDocEx extends VisitDoc {
	public static DocType instance() {
		if( instance == null )
			instance = new VisitDocEx();
		return instance;
	}
	
	@Override
	public boolean outOfScript() { return true; }
}
