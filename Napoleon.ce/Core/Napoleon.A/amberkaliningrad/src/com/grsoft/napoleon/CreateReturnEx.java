package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.widget.Spinner;


public class CreateReturnEx extends CreateReturn {
//	EditText edCause;
	
	@Override
	int getContentViewID() { return R.layout.createreturnex; }
	
	@Override
		protected void init(Return r, Org org) {
			super.init(r, org);
			r.prcType = ((OrgEx)org).priceType;
		}
	
	@Override
	protected void initCost(Return r) {
		ConfigImpl config = new ConfigImpl();
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		ArrayList<KeyValue> values = new ArrayList<KeyValue>();
		DialogHelper.loadSpinnerWithKey(config, "¬ид÷ены", values, spPrices, r.prcType);

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
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		KeyValue kv = (KeyValue) spPrices.getSelectedItem();
		if( kv != null )
			r.prcType = kv.key.toString();
	}
}
