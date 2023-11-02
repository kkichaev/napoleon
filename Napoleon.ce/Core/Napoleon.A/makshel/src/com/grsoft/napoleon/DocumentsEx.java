package com.grsoft.napoleon;

import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class DocumentsEx extends Documents {
	private View btnFastOrd;
	private boolean useFastOrder = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btnFastOrd = findViewById(R.id.btnFastOrd);
		btnFastOrd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				useFastOrder = true;
				btnNewDoc.performClick();
			}
		});
		
		showDialog(R.id.org_info_dlg);
	}
	
	@Override
	protected void createNewDoc() {
		if(useFastOrder && DocType.getCurDoc() == OrderDoc.instance()){
			useFastOrder = false;
			
			ConfigHelper.saveValidOrgTime(this, org.getData().id);
			
			CreatableDocument<?> doc = (CreatableDocument<?>)OrderDoc.instance().create();
			((OrderImpl)doc).initSilent(org.getData().id, GPSUtilNew.getLastKnownLocation(this));
			CreateOrder.initOrder((Order) doc.getData(), org.getData());
			TrdActionList.open(this, (OrderImpl) doc);
		}else
			super.createNewDoc();
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);
		
		btnFastOrd.setVisibility(docType == OrderDoc.instance() ? View.VISIBLE : View.GONE);
	}
	
	@Override protected int getContentViewID() { return R.layout.documentsex; }
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.org_info_dlg)
			return createOrgInfoDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createOrgInfoDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.info);
		builder.setView(View.inflate(this, R.layout.orginfodlg, null));
		builder.setPositiveButton(R.string.ok, null);
		return builder.create();
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == R.id.org_info_dlg)
			prepareOrgInfoDlg(dialog);
		else
			super.onPrepareDialog(id, dialog);
	}

	private void prepareOrgInfoDlg(Dialog dialog) {
		OrgEx o = (OrgEx) org.getData();
		
		TextView tv = (TextView) dialog.findViewById(R.id.tvCmnLimit);
		
		if(tv != null){
			String s = getString(R.string.common_limit, Util.IntToScaleStr(o.limit, Consts.SUM_SCALE));
			tv.setText(s);
		}
		
		tv = (TextView) dialog.findViewById(R.id.tvLimit);

		if(tv != null){
			String s = getString(R.string.cur_limit, Util.IntToScaleStr(o.limit -o.balance, Consts.SUM_SCALE));
			tv.setText(s);
		}
		
		tv = (TextView) dialog.findViewById(R.id.tvBalance);
		
		if(tv != null){
			String s = getString(R.string.cur_balance, Util.IntToScaleStr(o.balance, Consts.SUM_SCALE));
			tv.setText(s);
		}
	}
}
