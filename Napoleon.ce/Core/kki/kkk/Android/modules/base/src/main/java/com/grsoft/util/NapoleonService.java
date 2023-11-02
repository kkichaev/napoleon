package com.grsoft.util;

import android.util.Log;
import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.util.NapoleonServiceW;


public class NapoleonService extends NapoleonServiceW {
	@Override
	protected void initUpdatePrice(CfgNplW cfg) {
		final int range = ConfigHelper.getBkgRemnantsPeriod();
		
		Log.d(getClass().getCanonicalName(), "initUpdatePrice range: " + range);
		
		if(range > 0){
			priceUpdateTimer = new PriceUpdateTimer(){
				@Override protected int getDelayTime() { return range; }
			};
		}
	}
}
