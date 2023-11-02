package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.os.Bundle;
import android.widget.Spinner;

public class CreateReturnEx extends CreateReturn {
	@Override int getContentViewID() { return R.layout.createreturnex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Return r = doc.getData();
		ConfigImpl config = new ConfigImpl();		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerWithKey(config, "Организация", new ArrayList<KeyValue>(), spFirma, r.firmCode);
	}

	@Override
	protected void init(Return r, Org data) {
		super.init(r, data);
		r.firmCode = ((OrgEx)data).firmCode;
	}
	
	@Override
	protected void updateReturn(Return r) {
		super.updateReturn(r);

		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		KeyValue suppl = (KeyValue) spFirma.getSelectedItem();		
		if(suppl != null)
			r.firmCode = suppl.key.toString();
	}
}
