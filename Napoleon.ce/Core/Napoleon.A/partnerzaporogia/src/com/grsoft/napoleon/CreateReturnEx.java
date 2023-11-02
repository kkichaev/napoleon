package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.widget.Spinner;

public class CreateReturnEx extends CreateReturn {
	@Override
	int getContentViewID() { return R.layout.createreturnex; }

	@Override
	protected void initView() {
		ConfigImpl config = new ConfigImpl();
		ReturnEx incass = (ReturnEx) doc.getData();
		Spinner spDlvType = (Spinner) findViewById(R.id.spDlvType);

		DialogHelper.loadSpinnerFromConfig(config, "ТипОтгрузки", new ArrayList<CharSequence>(), spDlvType, incass.dlvType);
		
		config.close();
	}
	
	@Override
	protected void updateReturn(Return r) {
		super.updateReturn(r);
		CharSequence selVal = (CharSequence) ((Spinner) findViewById(R.id.spDlvType)).getSelectedItem();
		if(selVal != null)
			((ReturnEx)r).dlvType = selVal.toString();
	}
}
