package com.grsoft.napoleon;

import com.grsoft.dataobjects.IncassEx;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.os.Bundle;
import android.widget.Spinner;

public class IncassEditEx extends IncassEdit {
	@Override
	protected int getContentViewID() {
		return R.layout.incassex;
	}
	
	@Override
	protected void init(Bundle bundle) {
		super.init(bundle);
		
		FirmHelper.loadFirms((Spinner) findViewById(R.id.spFirma), ((IncassEx)doc.getData()).firmCode);
		findViewById(R.id.tvDate).setEnabled(false);
	}
	
	@Override
	protected void setDocument() {
		super.setDocument();
	
		KeyValue kv = (KeyValue) ((Spinner) findViewById(R.id.spFirma)).getSelectedItem();
		if( kv != null)
			((IncassEx)doc.getData()).firmCode = kv.key.toString();
	}
}
