package com.grsoft.dataobjects.impl;

import java.util.Date;

import com.grsoft.dataobjects.VisitEx;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;

public class ScriptImplEx extends ScriptImpl {
	@Override
	protected CreatableDocument<?> createDocument(String docType, Date docDate, ScriptDef def, ScriptDefItem defItem) {
		CreatableDocument<?> ret = super.createDocument(docType, docDate, def, defItem);
		if(isEditable() && defItem != null && defItem.name.length() > 0 && ret instanceof VisitImpl)
		{
			((VisitEx)ret.getData()).script = defItem.name;
		}
		return ret;
	}
}
