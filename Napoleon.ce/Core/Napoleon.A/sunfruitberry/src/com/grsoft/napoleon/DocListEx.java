package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.AgentMemoImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DispatchReturnsDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.DocFilterOnClickListener;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class DocListEx extends DocList {
	
	public static final String SHOW_RETURNS = "SHOW_RETURNS";
	
	boolean showNewReturns = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Bundle i = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		if(i != null && i.getBoolean(SHOW_RETURNS, false)) {
			DocType.setCurDoc(DispatchReturnsDoc.instance());
			showNewReturns = true;
		}
		
		super.onCreate(savedInstanceState);
	}
	
	protected DocType getDefaultDocType(){ 
		return showNewReturns ? DispatchReturnsDoc.instance() : OrderDoc.instance(); 
	}
	
	@Override
	protected int getDocStatusResource(CreatableDocument<?> doc) {
		if(doc instanceof AgentMemoImpl) {
			AgentMemoImpl am = (AgentMemoImpl)doc;
			if(am.isAppoved()) return R.drawable.memo_approve;
			if(am.isRejected()) return R.drawable.memo_reject;
		}
		return super.getDocStatusResource(doc);
	}

	View.OnClickListener callPhone = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			String phone = (String) arg0.getTag();
			if(phone != null && phone.length() > 0) {
				Intent intent = new Intent(Intent.ACTION_CALL,  Uri.parse(String.format("tel: %s", phone)));
				DocListEx.this.startActivity(intent);
			}
		}
	};
	
	@Override
	protected void drawData(View view, Document<?> doc, int position) {
		super.drawData(view, doc, position);
//		if(doc instanceof OrderImpl) {
//			OrderEx oe = (OrderEx) doc.getData();
//			if(oe.podPhone.length() > 0) {
//				TextView v = (TextView) view.findViewById(R.id.tvStatus);
//				String text = v.getText().toString();
//				v.setOnClickListener(callPhone);
//				v.setTag(oe.podPhone);
//				text += " <u>" + oe.podPhone + "</u>"; 
//				v.setText(Html.fromHtml(text));
//			}
//		}
	}
	
	protected DocFilterOnClickListener createDocListFilter() {
		return new DocFilterOnClickListener(this, true, false) {
			@Override
			protected void initData(boolean creatableFilter) {
				super.initData(creatableFilter);
				if(!data.contains(DispatchReturnsDoc.instance()))
					data.add(DispatchReturnsDoc.instance());
			}
		};
	}
	
	@Override
	protected DocListAdapter createListAdapter(DocType docType) {
		DocListAdapterEx ret = new DocListAdapterEx(this, docType, saveDatePeriod); 
		return ret;
	}
	
	class DocListAdapterEx extends DocListAdapter {
		public DocListAdapterEx(Context context, DocType docType, DatePeriod filter) {
			super(context, docType, filter);
		}
		
		@Override
		public com.grsoft.napoleon.documents.DocList fillDocList(DocType docType, String orgId, String order, DatePeriod dp) {
			if(showNewReturns && docType == DispatchReturnsDoc.instance()) {
				return docType.docList(null, order, "newDoc=1");
			}
			return super.fillDocList(docType, orgId, order, dp);
		}
	}
}
