package com.grsoft.napoleon.documents;

public class QuestionDocEx extends QuestionDoc {
	
	static public void init() {
		instance = new QuestionDocEx();
	}
	
	@Override
	public boolean outOfScript() {
		return false;
	}
}
