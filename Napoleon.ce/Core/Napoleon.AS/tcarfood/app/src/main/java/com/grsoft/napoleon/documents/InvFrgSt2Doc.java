package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.InvFrgSt2Impl;
import com.grsoft.napoleon.R;

public class InvFrgSt2Doc extends VisitDoc {
	public static String OBJECT_NAME = "InvFrgSt2";
	private static InvFrgSt2Doc theInstance;
	
	public InvFrgSt2Doc(){
		super(OBJECT_NAME, OBJECT_NAME, InvFrgSt2Impl.class);
	}
	
	public static InvFrgSt2Doc theInstance(){
		if (theInstance == null)
			theInstance = new InvFrgSt2Doc();
		
		return theInstance;
	}
	
	@Override
	public int getDocTitle() {
		return R.string.st2title;
	}
}
