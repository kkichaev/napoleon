package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImplEx.IStopRemark;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class DocumentsEx extends DocumentsPrint implements IStopRemark{
	
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
		return NOT_CHECK_ORG_BLOCKED ? false : super.isOrgBlocked(o, dt);
	}
	
	@Override
	public String getStopRemark() { return stopRemark; }

//	@Override
//	protected void adjustViewForDocType(DocType docType) {
//		if( docType == DebtDoc.instance() ) {
//			DocType.setCurDoc(docType);
//			DebetView.open(this, org.getData().id);
//			finish();
//		} else
//			super.adjustViewForDocType(docType);
//	}
	
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

}
