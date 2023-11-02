package com.grsoft.napoleon.documents;

import android.app.Activity;
import android.view.View;
import android.widget.Adapter;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.script.documents.ScriptDoc;


public class ScriptDocEx extends ScriptDoc {
	
	public ScriptDocEx(String docName, String objName, Class<? extends ScriptImpl> type) {
		super(docName, objName, type);
	}

	static public DocType instance(Class<? extends ScriptImpl> type) {
		instance = new ScriptDocEx(DOC_NAME, OBJ_NAME, type);
		return instance;
	}
	
	@Override
	public void setMainView(View view, LinesCountController linesController, OrgImpl orgImpl, OrgSumImpl orgSumImpl) {
		super.setMainView(view, linesController, orgImpl, orgSumImpl);
		view.findViewById(R.id.tvOrgSum).setVisibility(View.GONE);
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		super.setView(adapter, view, doc);
		view.findViewById(R.id.tvSum).setVisibility(View.GONE);
	}
	
	@Override
	public void viewOpened(Activity documentsView) {
		super.viewOpened(documentsView);
		View v = documentsView.findViewById(R.id.SumColumnTitle);
		
		if(v != null)
			v.setVisibility(View.GONE);
	}
}
