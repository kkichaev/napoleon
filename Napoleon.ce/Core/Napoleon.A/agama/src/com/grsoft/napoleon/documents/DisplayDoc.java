package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.DisplayImpl;
import com.grsoft.napoleon.R;

public class DisplayDoc extends DateDocType {
	
	public static final String OBJ_NAME =  "Display";
	
	static DisplayDoc instance = null;
	
	public static DocType instance() {
		if( instance == null )
			instance = new DisplayDoc();
		return instance;
	}
	
	DisplayDoc() {
		super("Выкладка", OBJ_NAME, DisplayImpl.class);
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.display_doc;
	}
}
