package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.AgentMemoImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgTaskExecImpl;
import com.grsoft.napoleon.documents.AgentMemoDoc;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.DocFilterOnClickListener;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class DocumentsEx extends Documents {
	@Override
	protected String getOrder(DocType docType) {
		return (docType.isCreatable() == false) ? "date desc" : "date desc, created desc";
	}

	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType == DebtDoc.instance() ) {
			DocType.setCurDoc(docType);
			DebetView.open(this, org.getData().id);
			finish();
		} else {
			super.adjustViewForDocType(docType);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if(DocType.getCurDoc() != OrderDoc.instance()) {
			menu.removeItem(R.id.itSendInvoice);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.itSendInvoice) {
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();		
			Document<?> doc = (Document<?>) adapter.getItem(menuInfo.position);
			if( doc != null ) {
				AgentMemoImpl memo = AgentMemoImpl.createSendInvoice(this, (OrderImpl)doc);
				memo.open(this);
			}
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override protected int getContextMenuId() { return R.menu.doc_context_menu_ex;	}
	
	
	View.OnClickListener callPhone = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			String phone = (String) arg0.getTag();
			if(phone != null && phone.length() > 0) {
				Intent intent = new Intent(Intent.ACTION_CALL,  Uri.parse(String.format("tel: %s", phone)));
				DocumentsEx.this.startActivity(intent);
			}
		}
	};
	
	protected com.grsoft.util.DocFilterOnClickListener createDocFilter() {
		List<DocTypeBase> filter = new ArrayList<DocTypeBase>();
		if(DocType.getCurDoc() == AgentMemoDoc.instance()) {
			filter.add(DebtDoc.instance());
			filter.add(AgentMemoDoc.instance());
		} else {
			filter.add(OrderDoc.instance());
			filter.add(VisitDoc.instance());
			filter.add(RemnantsDoc.instance());
			filter.add(QuestionDoc.instance());
			filter.add(IncassDoc.instance());
			filter.add(TaskDoneDoc.instance(OrgTaskExecImpl.class));
			filter.add(ReturnDoc.instance());
		}
		return new DocFilterOnClickListener(this, false, ScriptDefImpl.canScripting(), filter);
	}
	
	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		String order = getOrder(docType); 
		return new DocumentsAdapterEx(this, docType, id, order);
	}
	
	class DocumentsAdapterEx extends DocumentsAdapter {

		public DocumentsAdapterEx(Context context, DocType docType, String orgId, String order) {
			super(context, docType, orgId, order);
		}
		
		@Override
		public DocList fillDocList(DocType docType, String orgId, String order, DatePeriod dp) {
			if(docType == AgentMemoDoc.instance()) {
				OrgImpl oi = new OrgImpl();
				OrgEx oe = (OrgEx) oi.getData();
				oe.id = orgId;
				oi.read();
				oi.close();
				String where = "id in (select id from Org where ido='" + oe.ido + "')"; 
				return docType.docList(null, order, dp, where);
			}
			return docType.docList(orgId, order, dp);
		}
		
		@Override
		protected void setData(View view, Document<?> doc, int position) {
			super.setData(view, doc, position);
//			if(doc instanceof OrderImpl) {
//				OrderEx oe = (OrderEx) doc.getData();
//				if(oe.podPhone.length() > 0) {
//					TextView v = (TextView) view.findViewById(R.id.tvOther);
//					String text = v.getText().toString();
//					v.setOnClickListener(callPhone);
//					v.setTag(oe.podPhone);
//					text += "<br/><u>" + oe.podPhone + "</u>"; 
//					v.setText(Html.fromHtml(text));
//				}
//			}
		}
	}
}
