package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.MoveImpl;
import com.grsoft.napoleon.R;

public class MoveDoc extends DocType {
	static public final String DOC_NAME = "Перемещение";
	static public final String OBJ_NAME = "Move";
	static private MoveDoc instance = null;
	
	public MoveDoc(){
		super(DOC_NAME, OBJ_NAME, MoveImpl.class);
	}
	
	public static DocType instance() {
		if( instance == null )
			instance = new MoveDoc();
		return instance;
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.move_doc;
	}
	
	@Override
	public int getDocTitle() {
		return R.string.move_doc_title;
	}
}
