package com.grsoft.dataobjects.impl;

import java.util.Date;

import com.grsoft.dataobjects.VisitEx;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.ExtrasConst;

public class ScriptImplEx extends ScriptImpl {
	@Override
	protected CreatableDocument<?> createDocument(String docType, Date docDate, ScriptDef def, ScriptDefItem defItem) {
		CreatableDocument<?>  ret = super.createDocument(docType, docDate, def, defItem);
		if(ret instanceof VisitImpl && ret.getRowid() == ExtrasConst.INVALID_ROWID) {
			VisitEx ve = (VisitEx)ret.getData();
			if(ve.script.length() == 0 && defItem != null) {
				ve.script = defItem.name;
			}
		}
		return ret;
	}
}
