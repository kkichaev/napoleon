package com.grsoft.napoleon;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentMemo;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.MemoStatus;
import com.grsoft.dataobjects.MemoType;
import com.grsoft.dataobjects.impl.AgentMemoImpl;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.documents.AgentMemoDoc;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DispatchReturnsDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DocListEx extends DocList {
	
	public static final String SHOW_RETURNS = "SHOW_RETURNS";
	
	boolean showNewReturns = false;
	View filterDialog;
	
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

	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);
		updateFilterLayout();
	}

	@Override
	protected void postUpdateFilterView(View view) {
		filterDialog = view;

		super.postUpdateFilterView(view);

		ConfigImpl config = new ConfigImpl();
		Spinner spTheme = (Spinner) view.findViewById(R.id.spTheme);
		DialogHelper.loadSpinnerWithKeyW(config, "ТемыСлужебныхЗаписок", new ArrayList<KeyValue>(), spTheme, "", true);

		List<String> status = new ArrayList<>();
		for (MemoStatus ms : DbReader.fetch(MemoStatus.class))
			if (!status.contains(ms.status)) status.add(ms.status);

		Collections.sort(status);

		status.add(0,"");

		Spinner sp = (Spinner) view.findViewById(R.id.spStatus);
		sp.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.simple_spinner_layout, status));
		updateFilterLayout();
	}

	void updateFilterLayout() {
		if(filterDialog != null) {
			filterDialog.findViewById(R.id.memo_filter).setVisibility(DocTypeBase.getCurDoc() == AgentMemoDoc.instance() ?
					View.VISIBLE :
					View.GONE);
		}
	}

	@Override
	protected int getFilterLayout() {
		return R.layout.date_selectionex;
	}

	String theme = "";
	Set<Long> status = new HashSet<>();

	@Override
	protected void postFilterClick(AlertDialog alertDialog) {
		super.postFilterClick(alertDialog);

		theme = "";
		status.clear();

		theme = (String) ((KeyValue)((Spinner)alertDialog.findViewById(R.id.spTheme)).getSelectedItem()).key;
		String st = ((Spinner)alertDialog.findViewById(R.id.spStatus)).getSelectedItem().toString();

		if (st.length() > 0){
			for(AgentMemo m : DbReader.fetch(AgentMemo.class, String.format("status='%s'", status))){
				status.add(m.created.getTime());
			}
		}
	}

	@Override
	protected void adapterFilter(DatePeriod dp, String id) {
		super.adapterFilter(dp, id);

		List<Long> toRemove = new ArrayList<>();

		if (DocType.getCurDoc().equals(AgentMemoDoc.instance())){
			for(Document<?> d : adapter.documents){
				AgentMemo a = (AgentMemo) d.getData();

				if (theme.length() > 0){
					if (!a.topic.equals(theme)) {
						toRemove.add(d.getRowid());
						continue;
					}
				}

				if (status.size() > 0){
					if (!status.contains(d.getRowid()))
						toRemove.add(d.getRowid());
						continue;
				}
			}

			adapter.documents.removeDocuments(toRemove);
			adapter.notifyDataSetChanged();
		}
	}
}
