package com.grsoft.napoleon.dostavka;

import com.grsoft.dataobjects.impl.DWaybillDocumentImpl;
import com.grsoft.util.ExtrasConst;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;

public class DShipmentEdit extends DWaybillEdit {
	public static void open(Context context, DWaybillDocumentImpl<?> doc){
		Intent intent = new Intent(context, DShipmentEdit.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		intent.putExtra(DWaybillEdit.DOCTYPE, doc.getClass());
		context.startActivity(intent);
	}
	
	@Override protected int getLayoutID() { return R.layout.dshipmentedit;}

	@Override protected DialogFragment createItemEditDialog() { return new DWaybillItemEdit(); }
}
