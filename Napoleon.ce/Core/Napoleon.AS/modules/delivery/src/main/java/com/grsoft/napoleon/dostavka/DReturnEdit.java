package com.grsoft.napoleon.dostavka;

import com.grsoft.dataobjects.impl.DWaybillDocumentImpl;
import com.grsoft.util.ExtrasConst;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DReturnEdit extends DWaybillEdit {
	public static void open(Context context, DWaybillDocumentImpl<?> doc){
		Intent intent = new Intent(context, DReturnEdit.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		intent.putExtra(DWaybillEdit.DOCTYPE, doc.getClass());
		context.startActivity(intent);
	}
	
	@Override protected int getLayoutID() { return R.layout.dreturnedit; }

	@Override
	protected DialogFragment createItemEditDialog() {
		return new DWaybillItemEdit(){
			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
				View result = super.onCreateView(inflater, container, savedInstanceState);
				TextView tv = (TextView) result.findViewById(R.id.tvFactQty);
				tv.setText(R.string.accepted);
				return result;
			}
		};
	}

}
