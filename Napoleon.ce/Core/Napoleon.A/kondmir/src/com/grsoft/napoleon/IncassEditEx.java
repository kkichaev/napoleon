package com.grsoft.napoleon;

import java.util.ArrayList;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import android.os.Bundle;
import android.widget.Spinner;

public class IncassEditEx extends IncassEdit {
	private Spinner spFirma;
	@Override protected int getContentViewID() { return R.layout.incassex; }
	
	@Override
	protected void init(Bundle bundle) {
		super.init(bundle);
		spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(new ConfigImpl(), "Организация", new ArrayList<CharSequence>(), spFirma, ((IncassEx)doc.getData()).supplyer);
	}
	
	@Override
	protected void setDocument() {
		super.setDocument();
		((IncassEx)doc.getData()).supplyer = spFirma.getSelectedItemPosition();
	}
	

}
