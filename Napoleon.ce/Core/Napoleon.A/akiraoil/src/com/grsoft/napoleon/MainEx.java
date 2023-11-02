package com.grsoft.napoleon;

import com.grsoft.dataobjects.Agents;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.util.DocFilterOnClickListener;

public class MainEx extends Main {
	@Override
	protected void onResume() {
		if(Agents.isDealer()) {
			if(!DocFilterOnClickListener.HiddenTypes.contains(OrderDoc.instance())) {
				DocFilterOnClickListener.HiddenTypes.add(OrderDoc.instance());
			}
			DocType.removeType(OrderDoc.instance());
			if(DocType.getCurDoc() == OrderDoc.instance())
				DocType.setCurDoc(VisitDoc.instance());

			Features.COST_IN_PRESENTATION = false;
		} else {
			Features.COST_IN_PRESENTATION = true;
			
		}
		super.onResume();
	}

	@Override
	protected void defDocType() {
		if(Agents.isDealer()) {
			if(DocType.getCurDoc() == OrderDoc.instance())
				DocType.setCurDoc(VisitDoc.instance());
		} else
			super.defDocType();
	}
}
