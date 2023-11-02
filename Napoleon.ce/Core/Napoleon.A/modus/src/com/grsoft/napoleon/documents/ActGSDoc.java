package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.napoleon.R;


public class ActGSDoc extends DocType {
	private static String OBJ_NAME = "ActGSDoc";
	private static DocType instance;
	
	protected ActGSDoc() {
		super(OBJ_NAME, OBJ_NAME, VisitImplEx.class);
	}
	
	public static DocType instance(){
		if (instance == null)
			instance = new ActGSDoc();
		
		return instance;
	}

	@Override public int getDocTitle() { return R.string.actgsdoctitle; }
	
	@Override public int getResurceId() { return R.drawable.actgs; }
}
