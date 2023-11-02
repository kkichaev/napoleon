package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.IncassDoc;


public class IncassDocEx extends IncassDoc {
	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("QuestionDoc уже создан!");
		instance = new IncassDocEx();
	}
	
	public IncassDocEx(){
		
	}
	
	@Override
	public boolean outOfScript() {
		return false;
	}
}
