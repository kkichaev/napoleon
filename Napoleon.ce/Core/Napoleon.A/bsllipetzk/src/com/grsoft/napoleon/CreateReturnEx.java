package com.grsoft.napoleon;

import java.util.ArrayList;

import android.widget.Spinner;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class CreateReturnEx extends CreateReturn {
	@Override
	int getContentViewID() { return R.layout.createreturnex; }

	@Override
	protected void init(Return r, Org data) {
		super.init(r, data);
		r.supplyer = ((OrgEx)data).firm;
	}
	
	@Override
	protected void init() {
		ConfigImpl config = new ConfigImpl();
		
		Return r = doc.getData();
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "ќрганизаци€", new ArrayList<CharSequence>(), spFirma, r.supplyer);

		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		DialogHelper.loadSpinnerFromConfig(config, "¬ид÷ены", new ArrayList<CharSequence>(), spPrices, r.sumType);

		config.getData().key = "ћожно»змен€ть÷ену";
		try {
			if (config.read() && Integer.parseInt(config.getData().value) == 0)
				spPrices.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		config.close();
	}
	
	@Override
	protected void updateReturn(Return r) {
		super.updateReturn(r);
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		int suppl = spFirma.getSelectedItemPosition();
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		int costType = spPrices.getSelectedItemPosition();

		if( suppl >= 0 )
			r.supplyer = suppl;
		if( costType >= 0 )
			r.sumType = costType;
	}
}
