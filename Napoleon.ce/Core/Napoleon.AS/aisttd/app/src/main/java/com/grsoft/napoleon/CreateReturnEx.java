package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.os.Bundle;
import android.widget.Spinner;

public class CreateReturnEx extends CreateReturn {
	Spinner spCause ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		ConfigImpl ci = new ConfigImpl();
		spCause = (Spinner)findViewById(R.id.spCause);
		DialogHelper.loadSpinnerWithKey(ci, "ПричиныВозвратов", new ArrayList<KeyValue>(), 
				spCause, ((ReturnEx)doc.getData()).cause);
	}
	
	@Override
	protected void updateReturn(Return r) {
		super.updateReturn(r);
		
		KeyValue value = (KeyValue) spCause.getSelectedItem();
		
		if( value != null)
			((ReturnEx)doc.getData()).cause = value.key.toString();
	}
	
	@Override
	int getContentViewID() {
		return R.layout.createreturnex;
	}
}
