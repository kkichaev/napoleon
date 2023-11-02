package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Defect;
import com.grsoft.dataobjects.impl.DefectImpl;
import com.grsoft.napoleon.R;

public class DefectDoc extends OrderDoc {

	static public final String DOC_NAME = "Брак";
	static public final String OBJ_NAME = "Defect";
	static private DefectDoc instance = null;

	protected DefectDoc() {
		super(DOC_NAME, OBJ_NAME, DefectImpl.class);
		DataObjectInfo.getInstance().replaceTableName(Defect.class, "defects");
	}

	static public DocType instance() {
		if (instance == null)
			instance = new DefectDoc();
		return instance;
	}

	@Override
	public int getResurceId() {
		return R.drawable.defects_doc;
	}
	
	@Override
	public int getDocTitle() { return R.string.defect_doc_title; }
}
