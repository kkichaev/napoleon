package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

public class DocumentsEx extends Documents {
	private static final int ORG_IS_CREDITOR_DLG = R.id.org_is_creditor;
	public static int debet;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		debet = 0;
	}

	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);

		if( adapter != null &&  DocType.getCurDoc() instanceof OrderDoc){
			int sum = 0;
			int weight = 0;

			for( int i=0; i<adapter.getCount(); i++ ) {
				Document<?> d = (Document<?>) adapter.getItem(i);
				sum += d.sum();
				weight += ((OrderImplBase<?>)d).weight();
			}
			
			DocType.getCurDoc().updateTotalSum(this, sum, weight, 0, R.id.tvTotalSum);
		}
	}
	
	@Override
	protected void doCreate() {
		DocType dt = DocType.getCurDoc();
		if((dt == OrderDoc.instance() || dt == ScriptDoc.instance())
				&& orgIsCreditor(org.getData().id))
			showDialog(ORG_IS_CREDITOR_DLG);
		else
			super.doCreate();
	}

	private boolean orgIsCreditor(String id) {
		StringBuilder where = new StringBuilder();
		where.append("id='").append(id).append("' and payDate < ").append(Util.getDate().getTime());
		
		DataTraveler.travel(DeliveryEx.class, new DataTraveler.Travel<DeliveryEx>() {
			@Override
			public boolean travel(DataTraveler<DeliveryEx> item) {
				debet += item.data.sumD;
				return true;
			}
		}, where.toString());
		
		return debet > 0;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == ORG_IS_CREDITOR_DLG)
			return createOrgIsCreditorDlg();
		return super.onCreateDialog(id);
	}

	private Dialog createOrgIsCreditorDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.credit, Util.IntToScaleStr(debet, Consts.SUM_SCALE)));
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
	
	@Override
	protected DocFilterOnClickListener createDocFilter() {
		return new DocFilterOnClickListener(this){
			@Override
			protected void initData(boolean creatableFilter) {
				super.initData(creatableFilter);
				data.remove(WSOrderDoc.instance());
			}
		};
	}
}
