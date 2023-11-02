package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.os.Bundle;
import android.widget.Spinner;

public class IncassEditEx extends IncassEdit {
	@Override
	protected int getContentViewID() { return R.layout.incassex; }

	@Override
	protected void init(Bundle bundle) {
		super.init(bundle);
		
		ConfigImpl config = new ConfigImpl();
		IncassEx incass = (IncassEx) doc.getData();
		Spinner spDlvType = (Spinner) findViewById(R.id.spDlvType);

		DialogHelper.loadSpinnerFromConfig(config, "ТипОтгрузки", new ArrayList<CharSequence>(), spDlvType, incass.dlvType);
		
		config.close();
	}
	
	@Override
	protected void setDocument() {
		super.setDocument();
		IncassEx incass = (IncassEx) doc.getData();
		CharSequence selVal = (CharSequence) ((Spinner) findViewById(R.id.spDlvType)).getSelectedItem();
		if(selVal != null)
			incass.dlvType = selVal.toString();
	}
}
