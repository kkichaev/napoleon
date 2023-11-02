package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import android.annotation.SuppressLint;
import android.view.View;


public class UpdateDBEx extends UpdateDB {
	@Override
	protected int getContentView() { return R.layout.updatedbex; }
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		findViewById(R.id.cbRemains).setVisibility(View.GONE);
		findViewById(R.id.cbDebt).setVisibility(View.GONE);
		findViewById(R.id.cbPresent).setVisibility(View.GONE);
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void postSync(Boolean result) {
		if( result ) {
			ConfigImpl ci = new ConfigImpl();
			StringBuilder sb = new StringBuilder();
			if( ci.getValue(sb, "РазменнаяМонета") ) {
				String[] vals = sb.toString().split("\t");
				PriceImpl pi = new PriceImpl();
				Price p = pi.getData();
				p.id = vals[1];
				p.name = vals[0];
				p.srchName = vals[0].toUpperCase();
				p.color = NapoleonApp.MONEY_COLOR;
				pi.write();
			}
		}
	}
}
