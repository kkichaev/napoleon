package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

public class DocumentsEx extends Documents {
	private static final int ORG_IS_CREDITOR_DLG = R.id.org_is_creditor;
	
	@Override
	protected void doCreate() {
		DocType dt = DocType.getCurDoc();
		if((dt == OrderDoc.instance() || dt == ScriptDoc.instance()))
			showDialog(ORG_IS_CREDITOR_DLG);
		else
			super.doCreate();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == ORG_IS_CREDITOR_DLG){
			OrgEx oe = (OrgEx)org.getData();
			String message = "Долг клиента:<i>" + Util.IntToScaleStr(oe.balance, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</i><br>";
			if( oe.outDays > 0) {
				message += "Просрочка:<i>" + Util.IntToScaleStr(oe.outDays, 1) + "</i>";
			} else {
				message += "Нет просроченных долгов";
			}
			AlertDialog ad = (AlertDialog)dialog;
			
			ad.setMessage(Html.fromHtml(message));
			return;
		}
		super.onPrepareDialog(id, dialog);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == ORG_IS_CREDITOR_DLG)
			return createOrgIsCreditorDlg();
		return super.onCreateDialog(id);
	}

	private Dialog createOrgIsCreditorDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("");
		builder.setTitle(R.string.alert);
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				if (allowCreateDocWhithoutGpsPos || GPSUtilNew.isGpsPosValid())
					createNewDoc();
				else
					makeLocationAlert();
			}
		});
		return builder.create();
	}
	
	View.OnClickListener callPhone = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			String phone = (String)arg0.getTag();
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(String.format("tel: %s", phone)));
			startActivity(intent);
		}
	};
	
	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		String order = (docType == DebtDoc.instance()) ? "date" : "date desc, created desc"; 
		return new Adapter(this, docType, id, order);
	}
	
	class Adapter extends DocumentsAdapter {

		public Adapter(Context context, DocType docType, String orgId, String order) {
			super(context, docType, orgId, order);
		}
		
		@Override
		protected void setData(View view, Document<?> doc, int position) {
			super.setData(view, doc, position);
			if( doc instanceof OrderImplEx ) {
				OrderEx oe = (OrderEx)doc.getData();
				TextView tv = (TextView)view.findViewById(R.id.tvOther);
				if( oe.phone != null && oe.phone.length() > 0 ) {
					tv.setTag(oe.phone);
					tv.setOnClickListener(callPhone);
				} else {
					tv.setOnClickListener(null);
				}
			}
		}
	}
}
