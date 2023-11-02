package com.grsoft.napoleon;

import java.util.ArrayList;
import android.widget.Spinner;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;


public class CreateReturnEx extends CreateReturn{
	private ArrayList<CharSequence> firms = new ArrayList<CharSequence>();
	private Spinner spFirma;
	
	@Override int getContentViewID() { return R.layout.createreturnex;	}
	
	@Override
	protected void init(Return r, Org data) {
		super.init(r, data);
		r.supplyer = ((OrgEx)data).suppl;
	}
	
	@Override
	protected void initView(){
		ConfigImpl config = new ConfigImpl();
		spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "Организация", firms, spFirma, doc.getData().supplyer);
	}
	
	@Override
	protected void updateReturn(Return r) {
		super.updateReturn(r);
		r.supplyer = spFirma.getSelectedItemPosition();
	}
}
