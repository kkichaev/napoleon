package com.grsoft.napoleon;

import java.util.List;
import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.ServerInfoHitching;
import com.grsoft.database.ServerInfoHitchingCl;

import android.content.Intent;

public class UpdateDBEx extends UpdateDBPrint {
	public static String RELOAD_ACTION = "reload_action";
	

	@Override
	protected void postSync(Boolean result) {
		super.postSync(result);
		
		if (result) {
			sendBroadcast(new Intent(RELOAD_ACTION));
			CostStrategyEx.resetCache();

			SendDocsService.registerService(getApplicationContext());
		}
	}


	@Override
	protected Hitching getServerInfoHitching() {
		return new ServerInfoHitchingCl();
	}

	@Override
	protected List<Hitching> getPrezentHitching() {
		List<Hitching> result = super.getPrezentHitching();
//		result.add(0, new FolderPhotoHitching());
		return result;
	}
}
