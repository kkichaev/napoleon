package com.grsoft.napoleon.documents;


public class QuestionDocEx extends QuestionDoc {
	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("QuestionDoc уже создан!");
		instance = new QuestionDocEx();
	}
	
	public QuestionDocEx(){
		
	}
	
	@Override
	public boolean outOfScript() {
		return false;
	}
}
