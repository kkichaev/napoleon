package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

public class IncassEditEx extends IncassEdit {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnSend.setVisibility(View.GONE);
	}
	
	@Override
	protected int getContentViewID() { return R.layout.incassex; }
	
	@Override
	protected void init(Bundle bundle) {
		super.init(bundle);
		
		IncassEx ie = (IncassEx) doc.getData();
		ConfigImpl c = new ConfigImpl();
		DialogHelper.loadSpinnerFromConfig(c, "‘ормаќплаты", new ArrayList<CharSequence>(), 
				(Spinner)findViewById(R.id.spPayType), ie.payType);
		ArrayList<CharSequence> data = new ArrayList<CharSequence>();
		data.add("");
		DialogHelper.loadSpinnerFromConfig(c, "ѕриемƒенег", data, 
				(Spinner)findViewById(R.id.spAgent), ie.agent);
		c.close();
	}
	
	@Override
	protected void setDocument() {
		super.setDocument();
		
		CharSequence value = (CharSequence) ((Spinner)findViewById(R.id.spPayType)).getSelectedItem();
		if( value != null )
			((IncassEx) doc.getData()).payType = value.toString();
		
		value = (CharSequence) ((Spinner)findViewById(R.id.spAgent)).getSelectedItem();
		
		if( value != null )
			((IncassEx) doc.getData()).agent = value.toString();
	}
}
