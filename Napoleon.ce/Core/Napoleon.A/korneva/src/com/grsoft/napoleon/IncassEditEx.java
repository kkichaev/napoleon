package com.grsoft.napoleon;

import java.util.ArrayList;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Spinner;

public class IncassEditEx extends IncassEdit {
	CheckBox cbByCheck;
	@Override
	protected int getContentViewID() {
		return R.layout.incassex;
	}
	
	@Override
	protected void init(Bundle bundle) {
		super.init(bundle);
		
		cbByCheck = (CheckBox) findViewById(R.id.cbByCheck);
		
		ConfigImpl ci = new ConfigImpl();
		
		IncassEx ie = (IncassEx)doc.getData();
		DialogHelper.loadSpinnerFromConfig(ci, " анал—быта", new ArrayList<CharSequence>(), 
				(Spinner)findViewById(R.id.spChanel), ie.chanel);
		
		cbByCheck.setChecked(ie.check > 0);
	}
	
	@Override
	protected void setDocument() {
		super.setDocument();

		IncassEx ie = (IncassEx)doc.getData();
		Object val = ((Spinner)findViewById(R.id.spChanel)).getSelectedItem();
		if( val != null )
			ie.chanel = val.toString();
		
		ie.check = cbByCheck.isChecked() ? 1 : 0;
	}
}
