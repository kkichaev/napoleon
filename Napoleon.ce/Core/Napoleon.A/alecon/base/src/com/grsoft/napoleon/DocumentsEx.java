package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

public class DocumentsEx extends Documents {
	private static final int ORG_IS_CREDITOR_DLG = R.id.org_is_creditor;
	private int debet;

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
		DeliveryEx data = new DeliveryEx();
		DbReader reader = new DbReader();
		DbWriter.checkDBTable(DeliveryEx.class);
		boolean bdo = reader.select(data, 
				DataObjectInfo.getInstance().getTableName(DeliveryEx.class), 
				"id='" + id + "' and payDate < " + Util.getDate().getTime());
		
		debet = 0;
		while(bdo){
			debet += data.sumD;
			bdo = reader.selectNext(data);
		}
		
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
}
