package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.InvFrgSt3Impl;
import com.grsoft.napoleon.R;

public class InvFrgSt3Doc extends VisitDoc {
	public static String OBJECT_NAME = "InvFrgSt3";
	private static InvFrgSt3Doc theInstance;
	
	public InvFrgSt3Doc(){
		super(OBJECT_NAME, OBJECT_NAME, InvFrgSt3Impl.class);
	}
	
	public static InvFrgSt3Doc theInstance(){
		if (theInstance == null)
			theInstance = new InvFrgSt3Doc();
		
		return theInstance;
	}
	
	@Override
	public int getDocTitle() {
		return R.string.st3title;
	}
}
