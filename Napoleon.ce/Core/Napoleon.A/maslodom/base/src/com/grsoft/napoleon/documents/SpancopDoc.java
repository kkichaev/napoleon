package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.SpancopImpl;
import com.grsoft.napoleon.R;

public class SpancopDoc extends DocType {
	public static String OBJECT_NAME = "Spancop";
	static SpancopDoc instance;
	
	public static DocType instance() {
		if( instance == null )
			instance = new SpancopDoc();
		return instance;
	}
	
	protected SpancopDoc() {
		super(OBJECT_NAME, OBJECT_NAME, SpancopImpl.class);
	}

	@Override
	public boolean outOfScript() {
		return true;
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.spancop;
	}
	
	@Override
	public int getDocTitle() {
		return R.string.spancop_doc_title;
	}
}
