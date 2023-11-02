package com.grsoft.napoleon;

import java.util.ArrayList;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.util.DocFilterOnClickListener;



public class DocumentsEx extends Documents {
	protected void onlyVisitInit() {
		super.onlyVisitInit();
		btnDocFilter.setOnClickListener(new DocFilterOnClickListener(this){
			{
				filter = new ArrayList<DocTypeBase>();
				filter.add(VisitDoc.instance());
				filter.add(QuestionDoc.instance());
			}
		});
	}
	
	@Override
	protected String orgInfo(Org o) {
		String result =  super.orgInfo(o);
		OrgEx org = (OrgEx) o;
		
		if(org.pers.length() > 0)
			result += "<br>" + org.pers;
		
		return result;
	}
}
