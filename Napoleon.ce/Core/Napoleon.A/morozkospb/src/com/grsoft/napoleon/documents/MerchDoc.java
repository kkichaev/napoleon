package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.ScriptDefEx;
import com.grsoft.dataobjects.impl.MerchImpl;
import com.grsoft.napoleon.R;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.documents.CreateByScriptDef;

public class MerchDoc extends DocType implements CreateByScriptDef{
	private static String OBJ_NAME = "Merch";
	private static MerchDoc instance;
	
	protected MerchDoc() {
		super(OBJ_NAME, OBJ_NAME, MerchImpl.class);
	}
	
	@Override
	public int getDocTitle() {
		return R.string.merch_doc;
	}

	public static DocType instance() {
		if( instance == null )
			instance = new MerchDoc();
		return instance;
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.distr_doc;
	}

	@Override
	public Document<?> create(ScriptDef def, ScriptDefItem item) {
		MerchImpl res = (MerchImpl) create();
		res.getData().suppl = ((ScriptDefEx)def).suppl;
		
		return res;
	}
}
