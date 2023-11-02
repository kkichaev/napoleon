package com.grsoft.script.dataobjects.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.grsoft.dataobjects.ScriptEx;
import com.grsoft.dataobjects.impl.AgentTaskImpl;
import com.grsoft.dataobjects.impl.IncassImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.SVTaskImpl;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.TaskBeginDoc;
import com.grsoft.napoleon.documents.TaskEndDoc;
import com.grsoft.network.DocExportListener;
import com.grsoft.script.dataobjects.ScriptItem;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.Util;

public class ScriptImplEx extends ScriptImpl {
	public static final String SCRIPT_DATE = "com.grsoft.script.dataobjects.impl.ScriptImplEx.SCRIPT_DATE";
	public static final String SCRIPT_PREF = "com.grsoft.script.dataobjects.impl.ScriptImplEx.SCRIPT_PREF";
	public static final String SCRIPT_ID = "com.grsoft.script.dataobjects.impl.ScriptImplEx.SCRIPT_ID";
	
	public List<DocExportListener> getSendedDocuments() {
		List<DocExportListener> docs = new ArrayList<DocExportListener>();
		docs.add(new DocSendListner(ScriptDoc.OBJ_NAME, this));
		read();
		for(ScriptItem item : data.items) {
			if( item.state == ScriptItem.DOC_INITED ) {
				CreatableDocument<?> doc = createDocument(item.type, item.date, null);
				
				if(item.type.equals(TaskBeginDoc.OBJ_NAME )){
					docs.add(TaskBeginDoc.instance().getDirtyDocuments());
				}else if(item.type.equals(TaskEndDoc.OBJ_NAME)){
					docs.add(TaskEndDoc.instance().getDirtyDocuments());
				}else
					docs.add(new DocSendListner(item.type, doc));
			}
		}

		return docs;
	}
	
	@Override protected CreatableDocument<?>[] getDocuments() {
		CreatableDocument<?>[] result = super.getDocuments();
		int flags = ((ScriptEx)data).flags;
		
		final int EXPORTED = 1;
		final int TASKBEFOREDONE = 2;
		final int TASKAFTERDONE = 4;
		final int ORDEROUTOFPLAN = 8;
		final int INCASSOUTOFPLAN = 0x10;
		final int PHOTOBEFORE = 0x20; 
		final int PHOTOAFTER = 0x40;
		final int INTERRUPTED = 0x80;
		
		ScriptDefImpl defImpl = new ScriptDefImpl();
		defImpl.getData().id = data.scriptId;
		defImpl.read();
		defImpl.close();
		
		if(isExported())
			flags |= EXPORTED;
		
		int count = 0;
		int visit = 0;
		for(int i = 0; i < result.length; i++){
			CreatableDocument<?> d = result[i];
			if(d instanceof SVTaskImpl)
				flags |= TASKBEFOREDONE;
			else if(d instanceof AgentTaskImpl)
				flags |= TASKAFTERDONE;
			else if(d instanceof VisitImpl){
				if(visit == 1){
					Calendar calendar = Calendar.getInstance();
					d.getData().date = calendar.getTime();
					((ScriptEx)data).dateEnd = calendar.getTime();
					d.write();
					write();
					d.close();
					close();
				}
				
				if((flags & PHOTOBEFORE) == PHOTOBEFORE){
					flags |= PHOTOAFTER;
				} else
					flags |= PHOTOBEFORE;
				
				visit++;
			}
			
			if(d != null)
				count++;
		}
		
		if(defImpl.getData().items.size() != count)
			flags |= INTERRUPTED;
		
		if(result.length == 1 && result[0] instanceof OrderImpl)
			flags |= ORDEROUTOFPLAN;
		
		if(result.length == 1 && result[0] instanceof IncassImpl)
			flags |= INCASSOUTOFPLAN;
		
		((ScriptEx)data).flags = flags;
		
		return result;
	}
	
	@Override
	protected void postInit(Context context) {
		data.date = Util.getDateTime();
		((ScriptEx)data).dateEnd = data.date;
		write();
		SharedPreferences pref = context.getSharedPreferences(SCRIPT_PREF, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putLong(SCRIPT_DATE, data.date.getTime());
		editor.putString(SCRIPT_ID, data.id);
		editor.commit();
		close();
	}
}
