package com.grsoft.dataobjects.impl;

import java.util.Date;

import com.grsoft.dataobjects.ScriptDefItemEx;
import com.grsoft.dataobjects.VisitEx;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;

public class ScriptImplEx extends ScriptImpl {
	@Override
	protected CreatableDocument<?> createDocument(String docType, Date docDate, ScriptDef def, ScriptDefItem defItem) {
		CreatableDocument<?> ret = super.createDocument(docType, docDate, def, defItem);
		
		if(isEditable() && defItem != null && ret instanceof VisitImpl)
		{
			if (((ScriptDefItemEx)defItem).needSend > 0)
				((VisitEx)ret.getData()).needSend = 1;
			if (defItem.condParam.equals("allowGallery"))
				((VisitEx)ret.getData()).allowGallery = 1;
		}
		
		return ret;
	}
}
