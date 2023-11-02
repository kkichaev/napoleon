package com.grsoft.napoleon.documents;


public class VisitDocEx extends VisitDoc {
	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("DebtDoc уже создан!");
		instance = new VisitDocEx();
	}
	
	public boolean outOfScript() { return true; }
}
