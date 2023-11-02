package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.Selector;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.util.DocFilterOnClickListener;

public class DocumentsEx extends Documents {
	@Override
	protected DocFilterOnClickListener createDocFilter() {
		return new DocsFilter(this);
	}
	
	@Override
	protected void onlyVisitInit() {
		DocType cd = DocType.getCurDoc(); 
		if( cd != VisitDoc.instance() && cd != QuestionDoc.instance() ) {
			Napoleon.prevDocType = (DocType) DocType.getCurDoc();
			DocType.setCurDoc(VisitDoc.instance());
		}
	}
	
	class DocsFilter extends DocFilterOnClickListener {

		public DocsFilter(Selector selector) {
			super(selector);
		}
		
		@Override
		protected void initData(boolean creatableFilter) {
			if( org.getData().isPotencial() ) {
				if (data.size() == 0){
					data.add(VisitDoc.instance());
					data.add(QuestionDoc.instance());
				}				
			} else
				super.initData(creatableFilter);
		}
	}
}
