package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.widget.Spinner;

public class IncassEditEx extends IncassEdit {

	@Override
	protected int getContentViewID() { return R.layout.incass_ex; }
	
	@Override
	protected void setSum(int sum) {
		super.setSum(sum);
		
		IncassEx ie = (IncassEx) doc.getData();

		OrgImpl oi = new OrgImpl();
		OrgEx org = (OrgEx) oi.getData();		
		org.id = ie.id;
		oi.read();
		oi.close();
		if(ie.sum == 0)
			ie.supplyer = org.firm;

		ConfigImpl config = new ConfigImpl(); 
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "Организация", new ArrayList<CharSequence>(), spFirma, ie.supplyer);
		spFirma.setEnabled(org.firm < 0);
	}
	
	@Override
	protected void setDocument() {
		super.setDocument();
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		int sel = spFirma.getSelectedItemPosition();
		if(sel >= 0) {
			IncassEx ie = (IncassEx) doc.getData();
			ie.supplyer = sel;
		}
	}
}
