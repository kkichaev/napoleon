package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.script.documents.ScriptDoc;
import android.app.Activity;
import android.view.View;
import android.widget.Adapter;


public class ScriptDocEx extends ScriptDoc{
	public static void init(){
		instance = new ScriptDocEx();
	}
	
	@Override
	public void viewOpened(Activity documentsView) {
		super.viewOpened(documentsView);
		
		View v = documentsView.findViewById(R.id.SumColumnTitle);
		
		if(v != null)
			v.setVisibility(View.GONE);
		
		v = documentsView.findViewById(R.id.tvMainDocValColTitle);
		
		if(v != null)
			v.setVisibility(View.GONE);
		
		v = documentsView.findViewById(R.id.DateTitle);
		
		if (v != null)
			v.setVisibility(View.GONE);
	}
	
	@Override
	public void setMainView(View view, LinesCountController linesController, Org org, OrgSumImpl orgSumImpl){
		super.setMainView(view, linesController, org, orgSumImpl);
		
		View v = view.findViewById(R.id.tvOrgSum);
		
		if (v != null)
			v.findViewById(R.id.tvOrgSum).setVisibility(View.GONE);
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		super.setView(adapter, view, doc);
		
		view.findViewById(R.id.tvSum).setVisibility(View.GONE);	
	}
}
