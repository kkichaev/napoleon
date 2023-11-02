package com.grsoft.dataobjects.impl;

import java.util.List;
import com.grsoft.napoleon.documents.ATaskDoc;
import com.grsoft.napoleon.documents.TaskAnswerDoc;
import com.grsoft.network.DocExportListener;
import com.grsoft.script.dataobjects.impl.ScriptImpl;


public class ScriptImplEx extends ScriptImpl {
	public List<DocExportListener> getSendedDocuments() {
		List<DocExportListener> docs = super.getSendedDocuments();
		
		if(docs != null){
			docs.add(ATaskDoc.instance().getDirtyDocuments());
			docs.add(TaskAnswerDoc.instance().getDirtyDocuments());
		}
		
		return docs;
	};
}
