package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.DeliveryList;
import com.grsoft.network.NetworkAsyncTask;

import android.view.View;
import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	private static final String COMPLEX_PRICE = "СложнаяИсторияПродаж";
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		cbRemains.setChecked(false);
		cbRemains.setVisibility(View.GONE);
	}
	
	@Override
	protected boolean onFinishUpdate(NetworkAsyncTask task) {
		DeliveryList.clear();
		
		StringBuilder sb = new StringBuilder();
		ConfigImpl cfg = new ConfigImpl();
		
		if(cfg.getValue(sb, COMPLEX_PRICE)){
			CfgNplW config = (CfgNplW) ConfigManager.getConfig();
			config.isComplexSalesHistory = sb.toString().equals("1");
			ConfigManager.save();
		}
			
		return super.onFinishUpdate(task);
	}
}
