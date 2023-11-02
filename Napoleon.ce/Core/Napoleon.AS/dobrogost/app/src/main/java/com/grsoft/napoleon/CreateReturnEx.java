package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.widget.Spinner;

public class CreateReturnEx extends CreateReturn {
	protected void initCost(Return r) {
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		ConfigImpl config = new ConfigImpl();
		OrgEx o = (OrgEx) oi.getData();
		DialogHelper.loadSpinnerWithKey(config, "¬ид÷ены", values, spPrices, o.priceType);

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
	protected void init(Return r, Org data) {
		super.init(r, data);
		
		((ReturnEx)r).priceType = ((OrgEx)data).priceType;
	}
	
	@Override
	protected void updateReturn(Return r) {
		super.updateReturn(r);
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		KeyValue v = (KeyValue)spPrices.getSelectedItem();
		((ReturnEx)r).priceType = v.key.toString();
	}
}
