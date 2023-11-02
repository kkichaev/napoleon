package com.grsoft.ads.database;

import com.grsoft.ads.dataobjects.impl.TaskVisitImpl;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.network.DocExportListener;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;


public class TaskVisitHitching implements DocExportListener{
	private final static String OBJ_NAME = "TaskVisit";
	private DocList list;
	
	public TaskVisitHitching(){
		StringBuilder sb = new StringBuilder();
		sb.append("(([params] & ").append(ParamState.ofExported).append(" ) == 0)");
		sb.append(" and [readytosend] = 1");
		list = new DocList(TaskVisitImpl.class, sb.toString(), null);
	}
	
	@Override
	public void onStart() {}

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {}

	@Override
	public void onSave() {}

	@Override
	public void onEnd() {
		for( int i=0; i<list.getCount(); i++ ) {
			CreatableDocument<?> d = (CreatableDocument<?>)list.get(i);
			if( d != null ) d.setExported(true);
		}
	}

	@Override
	public String getObjectName() {	return OBJ_NAME; }

	@Override
	public DocList getDocuments() { return list; }
}
