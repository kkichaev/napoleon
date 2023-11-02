package com.grsoft.napoleon;

import java.util.ArrayList;

import android.widget.Spinner;

import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class CreateReturnEx extends CreateReturn {
	@Override
	int getContentViewID() { return R.layout.createreturnex; }

	@Override
	protected void init() {
		ConfigImpl config = new ConfigImpl();
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "Организация", new ArrayList<CharSequence>(), spFirma, doc.getData().supplyer);
		config.close();
	}
	
	@Override
	protected void updateReturn(Return r) {
		super.updateReturn(r);
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		int suppl = spFirma.getSelectedItemPosition();
		
		if(suppl >=0 )
			r.supplyer = suppl;
	}
}
