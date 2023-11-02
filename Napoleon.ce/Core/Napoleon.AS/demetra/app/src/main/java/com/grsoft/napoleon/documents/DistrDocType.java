package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.DistrDocImpl;
import com.grsoft.napoleon.R;

public class DistrDocType extends DateDocType {
	
	public static final String OBJ_NAME = "DistribGroupDoc";
	
	public static DistrDocType instance;
	
	public static DocType instance() {
		if( instance == null )
			instance = new DistrDocType();
		return instance;
	}
	
	private DistrDocType() {
		super("Дистрибуция", OBJ_NAME, DistrDocImpl.class);
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.distrib_doc;
	}
	@Override
	public int getResurce2Id() {
		return R.drawable.distrib_doc2;
	}
}
