package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImplEx.IStopRemark;
import com.grsoft.dataobjects.impl.SalesBanImpl;
import com.grsoft.dataobjects.impl.ServerInfoObjectImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.SalesDocEx;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;


public class DocumentsEx extends DocumentsPrint implements IStopRemark, SendResultListener {
	
	public static boolean NOT_CHECK_ORG_BLOCKED = false; 
	
	private String stopRemark;
	
	@Override
	protected String orgInfo(Org o) {
		String ret = super.orgInfo(o);
		OrgEx oe = (OrgEx)o;
		if(oe.vetis != OrgEx.VETIS_NONE) {
			ret += "<br/><b>Ветис статус: " + oe.vetisText() + "</b>"; 
		}
		return ret;
	}

	@Override
	protected boolean canCreateDoc(DocType docType) {
		if(!super.canCreateDoc(docType))
			return false;
		
		boolean ret = true;
		if(docType == SalesDoc.instance() || docType == OrderDoc.instance()) {
			OrgEx oe = (OrgEx)org.getData();
			ret = oe.canSale();
		}
		
		return ret;
	}
	
	@Override
	protected Dialog createWarningStopListDlg() {
		if( isBlocked() ) {
			return super.createWarningStopListDlg();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert);
		View view = View.inflate(this, R.layout.stoplistdlg, null);
		((TextView)view.findViewById(R.id.tvMsg)).setText(((OrgEx)org.getData()).stopMsg);
		final EditText edRemark = (EditText) view.findViewById(R.id.edRemark);
		view.findViewById(R.id.btnOK).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) { 
				stopRemark = edRemark.getText().toString().trim();
				
				if(stopRemark.length() == 0)
					Toast.makeText(v.getContext(), R.string.remark_need_value, Toast.LENGTH_SHORT).show();
				else{
					dismissDialog(DLG_WARNING_IF_ORG_IN_STOP_LIST);
					doCreate();
				}
			}
		});

		view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismissDialog(DLG_WARNING_IF_ORG_IN_STOP_LIST);
			}
		});
		
		builder.setView(view);
		return builder.create();
	}

	@Override
	protected boolean isOrgBlocked(Org o, DocType dt) {
		return isBlocked();
	}
	
	@Override
	public String getStopRemark() { return stopRemark; }

	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType == DebtDoc.instance() ) {
			DocType.setCurDoc(docType);
			DebetView.open(this, org.getData().id);
			finish();
		} else
			super.adjustViewForDocType(docType);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		CfgNplEx config = (CfgNplEx) ConfigManager.getConfig();
		if(config.simpleMode){
			btnDocFilter.setVisibility(View.GONE);
			btnNewDoc.setVisibility(View.GONE);
			btnGpsStatus.setVisibility(View.GONE);
		}
	}
	
	@Override protected void onlyVisitInit() {}

	@Override
	protected void doCreate() {
		if (DocType.getCurDoc() == SalesDoc.instance()) {
			if (SalesBanImpl.isOrgBanned(org.getData().id)) {
				Toast.makeText(this, R.string.sales_ban, Toast.LENGTH_SHORT).show();
				
				return;
			}
		}
		
		ServerInfoObjectImpl si = new ServerInfoObjectImpl();
		
		Date d = si.getValidDate();
		if (BuildConfig.DEBUG == false && DocType.getCurDoc() == SalesDoc.instance() && (
				d == null || (Util.getDate().getTime() < Util.resetTime(d).getTime())))
			Toast.makeText(this, R.string.invalid_date, Toast.LENGTH_SHORT).show();
		else
			super.doCreate();
	}

	@Override
	protected void docCreating() {
		if (DocType.getCurDoc() == SalesDoc.instance()) {
			boolean canSend = true;
			for(Document<?> d : ((SalesDocEx)SalesDoc.instance()).getDirtyDocuments(true).getDocuments()) {
				if(d.getId().equals(org.getData().id)) {
					canSend = false;
					break;
				}
			}
			if(!canSend)
				showDialog(R.id.ask_to_send_dlg);
		}else
			super.docCreating();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.ask_to_send_dlg)
			return createAskToSendDlg();
		return super.onCreateDialog(id);
	}

	private Dialog createAskToSendDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.warning);
		builder.setMessage(R.string.warning_send_docs);
		builder.setPositiveButton(R.string.ok, (d,w)-> doUpdate());
		builder.setNegativeButton(R.string.no, (d,w)->super.docCreating());
		return builder.create();
	}

	private void doUpdate() {
		new DocumentSender(this, null, SalesDoc.instance().getDirtyDocuments(), this).execute((Void[])null);
	}

	@Override
	public void postSendExecute(boolean result) {
		if (result)
			super.docCreating();
	}
}
