package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobject.AgentPrefixEx;
import com.grsoft.dataobject.OrgEx;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

public class CreateSalesEx extends CreateSales {
	@Override
	protected void init(Sales s, Org org) {
		s.prcType = ((OrgEx)org).prcType;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Sales s = salesImpl.getData();
		
		CheckBox cb = (CheckBox)findViewById(R.id.cbWhiteDoc);
		cb.setChecked((s.params & ParamState.ofCash) != 0);
		
		if(!editMode) {
			cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

					String curNumber = edNumber.getText().toString();
					AgentPrefixEx ap = (AgentPrefixEx) AgentPrefix.get();
					if( ap != null ) {
						String newPrefix = (isChecked) ? ap.prefix : ap.prefixAdd;
						String current = (isChecked) ? ap.prefixAdd : ap.prefix;
						
						if(curNumber.startsWith(current)) {
							curNumber = newPrefix + curNumber.substring(current.length());
							edNumber.setText(curNumber);
						}
					}					
				}
			});
		}
		
		ConfigImpl cfg = new ConfigImpl();
		Spinner sp = (Spinner)findViewById(R.id.spPrices);
		DialogHelper.loadSpinnerWithKey(cfg, "¬ид÷ены", new ArrayList<KeyValue>(), sp, s.prcType);
		
		cfg.getData().key = "ћожно»змен€ть÷ену";
		try {
			if (cfg.read() && Integer.parseInt(cfg.getData().value) == 0)
				sp.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		cfg.close();
	}
	
	@Override
	protected void postOkDone(Sales sales) {
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		int costType = spPrices.getSelectedItemPosition();
		if(costType > 0) {
			KeyValue kv = (KeyValue) spPrices.getSelectedItem();
			Sales s = salesImpl.getData();
			s.sumType = costType;
			s.prcType = kv.key.toString();
		}
	}
	
	@Override protected int getSalesLayoutId() { return R.layout.createsalesex; }
}
