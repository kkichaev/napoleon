package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.dataobjects.impl.ScriptImpl;


public class ScriptEditEx extends ScriptEdit {
	@Override
	protected CreatableDocument<?> openFirstItem(ScriptImpl scriptImpl, ScriptDef def, ScriptDefItem item, DocType dt) {
		return null;
	}
	
	protected String orgInfo(Org o) {
		String ret = super.orgInfo(o);
		
		String info = ((OrgEx)o).getInfo();
		if(info.length() > 0)
			ret += "<br>" + info;
		
		return ret; 
	}
}
