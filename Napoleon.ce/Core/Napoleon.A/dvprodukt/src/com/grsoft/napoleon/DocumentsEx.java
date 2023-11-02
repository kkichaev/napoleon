package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {
	private static final int ORG_DEBT_WARNING_DLG = R.id.org_debt_warning_dlg;
	boolean hideDebtWarningMessage = false;

	@Override
	protected void createNewDoc() {
		DocType dt = (DocType) DocType.getCurDoc();
		if(!hideDebtWarningMessage &&
				dt.equals(OrderDoc.instance()) && ((OrgEx)org.getData()).debt > 0)
			showDialog(ORG_DEBT_WARNING_DLG);
		else
			super.createNewDoc();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case ORG_DEBT_WARNING_DLG: return createOrgDebtWarningDlg();
		default: return super.onCreateDialog(id);
		}
	}

	private Dialog createOrgDebtWarningDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert);
		builder.setMessage(getString(R.string.org_debt_warning, 
				Util.IntToScaleStr(((OrgEx)org.getData()).debt, Consts.SUM_SCALE)));
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				hideDebtWarningMessage = true;
				createNewDoc();
			}
		});
		
		return builder.create();
	}
}
