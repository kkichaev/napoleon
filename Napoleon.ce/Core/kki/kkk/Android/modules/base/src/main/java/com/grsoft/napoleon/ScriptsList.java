package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.DatePeriod;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ScriptsList extends DocList {
	public static Class<? extends Activity> scriptsListActivity = ScriptsList.class;
	
	static void open(Context context) {
		Intent i = new Intent(context, scriptsListActivity);
		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.linearLayout1).setVisibility(View.GONE);
	}
	
	@Override
	protected DocListAdapter createListAdapter(DocType docType) {
		return new DocListAdapter(this, ScriptDoc.instance(), null){
			@Override
			public com.grsoft.napoleon.documents.DocList fillDocList(DocType docType, String orgId, String order, DatePeriod dp) {
				com.grsoft.napoleon.documents.DocList list = super.fillDocList(ScriptDoc.instance(), orgId, order, dp);
				
				List<Long> remove = new ArrayList<Long>(); 
				
				for(Document<?> d : list){
					if(((ScriptImpl)d).isComplete())
						remove.add(d.getRowid());
				}
				
				list.removeDocuments(remove);
				
				return list;
			}
		};
	}
	
	@Override
	protected void onResumeAdapter() {
		adapter.fetchByPeriod(ScriptDoc.instance(), null, null, null, null);
		super.onResumeAdapter();
	}
}
