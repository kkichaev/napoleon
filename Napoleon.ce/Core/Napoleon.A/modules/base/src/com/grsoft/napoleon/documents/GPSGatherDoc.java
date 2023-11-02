package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.GPSGatherImpl;
import com.grsoft.napoleon.R;


public class GPSGatherDoc extends DateDocType {
	public static String OBJ = "GPSGather";
	private static DocType instance;
	
	static public DocType instance() {
		if( instance == null )
			instance = new GPSGatherDoc();
		return instance;
	}
	
	protected GPSGatherDoc() {
		super(OBJ, OBJ, GPSGatherImpl.class);
	}

	@Override public int getDocTitle() { return R.string.orgcoord_title; }
	@Override public int getResurceId() {	return R.drawable.gpsgather; }
}


