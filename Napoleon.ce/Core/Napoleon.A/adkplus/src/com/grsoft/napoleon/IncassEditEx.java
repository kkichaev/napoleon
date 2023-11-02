package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;

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
	
		ConfigImpl config = new ConfigImpl();
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "Организация", new ArrayList<CharSequence>(), 
				spFirma, ((IncassEx)doc.getData()).supplyer);
		config.close();
	}
	
	@Override
	protected void setDocument() {
		super.setDocument();
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		((IncassEx)doc.getData()).supplyer = spFirma.getSelectedItemPosition();
	}
}
