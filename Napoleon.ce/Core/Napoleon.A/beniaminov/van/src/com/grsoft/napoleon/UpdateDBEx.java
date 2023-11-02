package com.grsoft.napoleon;

import android.content.Intent;

public class UpdateDBEx extends UpdateDBPrint {
	public static String RELOAD_ACTION = "reload_action";
	

	@Override
	protected void postSync(Boolean result) {
		super.postSync(result);
		
		if (result) {
			sendBroadcast(new Intent(RELOAD_ACTION));
			CostStrategyEx.resetCache();
		}
	}
}
