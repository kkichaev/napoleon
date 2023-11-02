package com.grsoft.script.documents;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.network.DocExportListener;
import com.grsoft.script.dataobjects.impl.ScriptImpl;

public class ScriptDoc extends DocType {
	static public final String DOC_NAME = "Визит";
	static public final String OBJ_NAME = "ScriptDoc";

	static protected ScriptDoc instance = null;
	
	protected ScriptDoc() { super(DOC_NAME, OBJ_NAME, ScriptImpl.class);	}
	
	protected ScriptDoc(String name, String objName, Class<? extends Document<?>> docClass) { 
		super(name, objName, docClass);
	}

	static public DocType instance() {
		if( instance == null ) {
			instance = new ScriptDoc();
		}
		
		Features.SCRIPT_DOC = true;
		return instance;
	}
	
	
	static public DocType instance(Class<? extends ScriptImpl> type) {
		instance = new ScriptDoc(DOC_NAME, OBJ_NAME, type);
		return instance;
	}
	
	@Override public int getResurceId() { return R.drawable.script_doc; }
	
	@Override
	public DocExportListener getDirtyDocuments() {
		List<Long> needRemove = new ArrayList<Long>();
		DocExportListener result = super.getDirtyDocuments();
		
		if( result != null ) {
			//обновим сумму документа
			for(Document<?> d : result.getDocuments()){
				if(d instanceof ScriptImpl){
					ScriptImpl s = ((ScriptImpl)d);
					long c = s.calcDocSum();
					
					if(s.getData().sum != c){
						s.getData().sum = c;
						s.write();
						s.close();
					}
					if(Features.DONT_SEND_UNCOMPLETE_SCRIPTS && !s.isComplete())
						needRemove.add(s.getRowid());
				}
			}
		}
		
		if(needRemove.size() > 0)
			result.getDocuments().removeDocuments(needRemove);
		return result;
	}
	
	@Override
	public int getDocTitle() {
		return R.string.script_doc_title;
	}
	
	@Override
	public int getResurce2Id() {
		return R.drawable.script_doc_2;
	}
}
