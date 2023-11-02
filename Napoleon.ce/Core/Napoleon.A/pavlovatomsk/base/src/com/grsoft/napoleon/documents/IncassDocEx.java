package com.grsoft.napoleon.documents;


public class IncassDocEx extends IncassDoc {
	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("IncassDocEx уже создан!");
		instance = new IncassDocEx();
	}
	
	@Override public boolean outOfScript() { return false; }
}
