package com.grsoft.napoleon.documents;

public class IncassDocEx extends IncassDoc {
	public static void init() {
		instance = new IncassDocEx();
	}
	
	@Override public boolean outOfScript() { return false; }
}
