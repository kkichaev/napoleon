package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.PlanogramImpl;
import com.grsoft.napoleon.R;

public class PlanogramDoc extends DocType {
	static protected PlanogramDoc instance = null;
	
	protected PlanogramDoc() { super("Планограмма", "Planogram", PlanogramImpl.class);} 
	
	static public DocType instance() {
		if( instance == null )
			instance = new PlanogramDoc();
		return instance;
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.planogram_doc;
	}
	
	@Override
	public int getResurce2Id() {
		return R.drawable.planogram_doc;
	}
}
