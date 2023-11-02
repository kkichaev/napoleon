package com.grsoft.napoleon;

import com.grsoft.dataobjects.Return;
import com.grsoft.util.view.dialog_helper.KeyValue;
import android.widget.Spinner;

public class CreateReturnEx extends CreateReturn {
	@Override
	int getContentViewID() {
		return R.layout.createreturnex;
	}
	
	@Override
	protected void init() {
		FirmHelper.loadFirms((Spinner) findViewById(R.id.spFirma), doc.getData().firmCode);
	}

	@Override
	protected void updateReturn(Return r) {
		super.updateReturn(r);
		KeyValue kv = (KeyValue) ((Spinner) findViewById(R.id.spFirma)).getSelectedItem();
		if( kv != null)
			r.firmCode = kv.key.toString();
	}
}
