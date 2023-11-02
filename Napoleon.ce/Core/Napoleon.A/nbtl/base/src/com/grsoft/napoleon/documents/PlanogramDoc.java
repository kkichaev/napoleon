package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.PlanogramImpl;
import com.grsoft.napoleon.R;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.documents.CreateByScriptDef;

public class PlanogramDoc extends DateDocType implements CreateByScriptDef{
	static private PlanogramDoc instance = null;
	
	private static final String DOC_NAME = "Планограмма";
	private static final String OBJ_NAME = "Planogram";
	
	public static DocType instance() {
		if( instance == null )
			instance = new PlanogramDoc();
		return instance;
	}
	
	protected PlanogramDoc() {
		super(DOC_NAME, OBJ_NAME, PlanogramImpl.class);
	}
	
	@Override
	public int getDocTitle() {	return R.string.planogram_doc_title; }
	
	@Override
	public int getResurceId() { return R.drawable.planogram_doc; }

	@Override
	public Document<?> create(ScriptDef def,  ScriptDefItem item) {
		PlanogramImpl result = (PlanogramImpl) create();
		result.getData().def = item.condParam;
		
		return result;
	}
}
