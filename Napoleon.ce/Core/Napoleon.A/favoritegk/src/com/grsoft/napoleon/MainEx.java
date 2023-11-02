package com.grsoft.napoleon;

import com.grsoft.dataobjects.IncassDebDistrEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.IncassDebDistrImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.FoldersMainAdapter.ViewData;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class MainEx extends Main {
	@Override
	protected void onResume() {
		super.onResume();
		initIncass();
	}
	
	protected void initIncass() {
		DocExportListener e =  IncassDoc.instance().getDirtyDocuments();
		
		if( e != null ) {
			OrgImpl org = new OrgImpl();
			
			for(Document<?> d : e.getDocuments()){
				org.read("id", d.getId());
				
				if (((OrgEx)org.getData()).brak == 1){
					IncassDebDistrImpl s = (IncassDebDistrImpl)d;
					if (((IncassDebDistrEx)s.getData()).brak.trim().length() == 0) {
						s.open(this);
						break;
					}
				}
			}
		}
		
	}
	
	public View getFolderMainView(View view, int pos, ViewData data){
		if (view == null || view.getId() != getFolderRowID())
			view = View.inflate(this, getFolderRowID(), null);
		
		setOrgBackground(pos, null, view);
		
		TextView tv = (TextView)view.findViewById(R.id.tvOrgName);
		
		if(tv != null){
			tv.setText(data.name);
			linesController.prepareTextView(tv);
			tv.setTextColor(Color.BLACK);
		}
		
		tv = (TextView)view.findViewById(R.id.tvOrgSum);
		
		if(tv != null){
			String text = Integer.toString(data.ids.size());
			tv.setText(text);
			tv.setTextColor(getResources().getColor(R.color.grey));
		}
		
		return view;
	}
}
