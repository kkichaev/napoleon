package com.grsoft.napoleon.dostavka;

import com.grsoft.dataobjects.impl.DWaybillDocumentImpl;
import com.grsoft.util.ExtrasConst;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class DShipmentEdit extends DWaybillEdit implements OnClickListener, RejectAction {
	
	public static Class<? extends DShipmentEdit> activity = DShipmentEdit.class;
	
	public static void open(Context context, DWaybillDocumentImpl<?> doc){
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		intent.putExtra(DWaybillEdit.DOCTYPE, doc.getClass());
		context.startActivity(intent);
	}
	
	@Override protected int getLayoutID() { return R.layout.dshipmentedit;}

	@Override protected DialogFragment createItemEditDialog() { return new DWaybillItemEdit(); }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnOK).setOnClickListener(this);
		findViewById(R.id.btnReject).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnOK) {
			onAccept();
		}else if (v.getId() == R.id.btnReject) { 
			onReject();
		}
	}

	protected void onAccept() {
		doc.setReadyToSend();
		doc.write();
		doc.close();
		finish();
	}
	
	protected void onReject() {
		new RejectDialog().show(getFragmentManager(), RejectDialog.class.toString());
	}

	
	@Override
	public void doReject(String remark) {
		doc.getData().remark = remark;
		doc.setRejected();
		doc.write();
		doc.close();
	}	
}
