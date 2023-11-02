package com.grsoft.napoleon.documents;

import com.grsoft.network.DocExportListener;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class DocSendListner implements DocExportListener {

	protected DocList list;
	String objName;
	
	public DocSendListner(String objName, CreatableDocument<?> docd) {
		this(objName, docd, docd.getRowid());
	}

	public DocSendListner(String objName, CreatableDocument<?> doc, long rid) {
		list = new DocList(doc, rid);
		this.objName = objName;
	}
	
	public DocSendListner(String objName, DocList docList) {
		list = docList;
		this.objName = objName;
	}
	
	public DocSendListner(String objName, Class<? extends CreatableDocument<?>> docType, String fieldName, int exportFlag) {
		this(objName, docType, "(([" + fieldName + "] & " + Integer.toString(exportFlag) + " ) == 0)");
//		String where = "(([" + fieldName + "] & " + Integer.toString(exportFlag) + " ) == 0)";
//		list = new DocList(docType, where, null);
//		this.objName = objName;
	}
	
	public DocSendListner(String objName, Class<? extends CreatableDocument<?>> docType, String where) {
		list = new DocList(docType, where, null);
		this.objName = objName;
	}
	
	@Override
	public DocList getDocuments() { return list; }

	@Override
	public String getObjectName() { return objName;	}

	@Override
	public void onEnd() {
		for( int i=0; i<list.getCount(); i++ ) {
			CreatableDocument<?> d = (CreatableDocument<?>)list.get(i);
			if( d != null ) d.setExported(true);
		}
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {}

	@Override
	public void onSave() { }

	@Override
	public void onStart() {	}
}
