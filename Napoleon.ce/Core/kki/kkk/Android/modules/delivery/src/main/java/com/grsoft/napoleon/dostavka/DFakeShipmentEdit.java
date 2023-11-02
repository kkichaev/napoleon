package com.grsoft.napoleon.dostavka;

import com.grsoft.dataobjects.impl.DWaybillDocumentImpl;
import com.grsoft.util.ExtrasConst;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DFakeShipmentEdit extends DShipmentEdit {
	public static void open(Context context, DWaybillDocumentImpl<?> doc){
		Intent intent = new Intent(context, DFakeShipmentEdit.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		intent.putExtra(DWaybillEdit.DOCTYPE, doc.getClass());
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		findViewById(R.id.btnOK).setVisibility(View.GONE);
		findViewById(R.id.btnReject).setVisibility(View.GONE);
	}
}
