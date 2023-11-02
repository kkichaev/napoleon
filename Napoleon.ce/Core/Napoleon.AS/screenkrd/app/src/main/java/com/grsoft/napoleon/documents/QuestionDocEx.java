package com.grsoft.napoleon.documents;


public class QuestionDocEx extends QuestionDoc {
	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("QuestionDocEx уже создан!");
		instance = new QuestionDocEx();
	}
	
	@Override
	public boolean outOfScript() { return false; }
}
