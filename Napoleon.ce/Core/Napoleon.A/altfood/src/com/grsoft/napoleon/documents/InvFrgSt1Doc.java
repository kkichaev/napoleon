package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.InvFrgSt1Impl;
import com.grsoft.napoleon.R;

public class InvFrgSt1Doc extends VisitDoc {
	public static String OBJECT_NAME = "InvFrgSt1";
	private static InvFrgSt1Doc theInstance;
	
	public InvFrgSt1Doc(){
		super(OBJECT_NAME, OBJECT_NAME, InvFrgSt1Impl.class);
	}
	
	public static InvFrgSt1Doc theInstance(){
		if (theInstance == null)
			theInstance = new InvFrgSt1Doc();
		
		return theInstance;
	}
	
	@Override
	public int getDocTitle() {
		return R.string.st1title;
	}
}
