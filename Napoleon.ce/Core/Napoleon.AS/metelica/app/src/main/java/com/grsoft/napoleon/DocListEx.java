package com.grsoft.napoleon;

import android.view.View;
import android.widget.TextView;
import com.grsoft.dataobjects.OrderDecision;
import com.grsoft.dataobjects.impl.OrderDecisionImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;


public class DocListEx extends DocList {
	protected int getDocStatusResource(CreatableDocument<?> doc) {
		if(doc instanceof OrderImplEx){
			OrderDecision decision = OrderDecisionImpl.getDecision(doc.getData().created);
			if(decision != null){
				if(decision.decision == 1){
					return R.drawable.reject;
				}else if(decision.decision == 2){
					return R.drawable.toedit;
				}
			}
		}
		
		return doc.isProceeded() ? R.drawable.pcd : 
				doc.isExported() ? R.drawable.sent : 
				R.drawable.notsend;
	}
	
	protected DocListAdapter createListAdapter(DocType docType){
		return new DocListAdapter(this, docType, saveDatePeriod, R.layout.docs_list_row2ex);
	}
	
	@Override
	protected void drawData(View view, Document<?> doc, int position) {
		super.drawData(view, doc, position);
		
		TextView tv = (TextView) view.findViewById(R.id.tvComment);
		
		if(tv != null){
			tv.setVisibility(View.GONE);
			
			if(doc instanceof OrderImplEx){
				OrderDecision decision = OrderDecisionImpl.getDecision(((OrderImplEx)doc).getData().created);
				
				if(decision != null && decision.remark.length() > 0){
					tv.setVisibility(View.VISIBLE);
					tv.setText(decision.remark);
				}
			}
		}
	}
}
