package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.widget.EditText;
import android.widget.Spinner;

public class CreateReturnEx extends CreateReturn {
	@Override int getContentViewID() { return R.layout.createreturnex; }

	@Override
	protected void initCost(Return r) {
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		ConfigImpl config = new ConfigImpl();
		DialogHelper.loadSpinnerWithKey(config, "¬ид÷ены", new ArrayList<KeyValue>(), spPrices, r.prcType);

		Spinner spCause = (Spinner)findViewById(R.id.spRetCause);
		DialogHelper.loadSpinnerWithKey(config, "ѕричины¬озврата", new ArrayList<KeyValue>(), spCause, ((ReturnEx)r).cause);

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
		r.date = dateHandler.getDate();
		r.remark = ((EditText)findViewById(R.id.edNotes)).getText().toString();
		
		Spinner sp = (Spinner) findViewById(R.id.spPrices);
		KeyValue sel = (KeyValue) sp.getSelectedItem();
		if( sel != null) {
			r.sumType = sp.getSelectedItemPosition();
			r.prcType = sel.key.toString();
		}
		
		sp = (Spinner) findViewById(R.id.spRetCause);
		sel = (KeyValue) sp.getSelectedItem();
		if( sel != null) {
			((ReturnEx)r).cause = sel.key.toString();
		}
	}
	
	@Override
	protected void init(Return r, Org o) {
		ConfigImpl config = new ConfigImpl();
		Config c = config.getData();
		c.key = "¬ид÷ены";
		config.read();
		config.close();
		
		ArrayList<KeyValue> kv = new ArrayList<KeyValue>();
		DialogHelper.makeListWithKey(c.value, kv, null);
		if(o.costype >= 0 && o.costype < kv.size()) {
			r.prcType = kv.get(o.costype).key.toString();
			r.sumType = o.costype;
		}
	}
}
