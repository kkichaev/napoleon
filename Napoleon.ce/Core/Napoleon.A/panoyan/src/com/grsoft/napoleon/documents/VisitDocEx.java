package com.grsoft.napoleon.documents;


public class VisitDocEx extends VisitDoc {
	public static void init() {
		instance = new VisitDocEx();
	}
	
	@Override
	public boolean outOfScript() { return true;	}
}
