package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.util.DocFilterOnClickListener;

import java.util.ArrayList;

public class DocumentsEx extends Documents {

    @Override 	protected void onlyVisitInit() {
		DocType cd = DocType.getCurDoc();
		if( cd != VisitDoc.instance() && cd != QuestionDoc.instance() ) {
			Napoleon.prevDocType = (DocType) DocType.getCurDoc();
			DocType.setCurDoc(VisitDoc.instance());
		}

		btnDocFilter.setOnClickListener(new DocFilterOnClickListener(this){{
			filter = new ArrayList<>();
			filter.add(VisitDoc.instance());
			filter.add(QuestionDoc.instance());
		}});
    }
}
