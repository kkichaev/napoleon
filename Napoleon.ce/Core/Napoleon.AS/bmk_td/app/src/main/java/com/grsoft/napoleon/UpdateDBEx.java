package com.grsoft.napoleon;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Agreement;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Util;

import java.util.Date;
import java.util.List;

public class UpdateDBEx extends UpdateDB {
	public static final String SYNCDATE = "syncdate";

    @Override
	protected void postSync(Boolean result) {
		if(result){
			CostStrategyEx.refreshCash();
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor ed = sp.edit();
			ed.putLong(SYNCDATE, Util.resetTime(new Date()).getTime());
			ed.commit();
		}
	}

	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new RcvNewHitching(Agreement.class));
		return ret;
	}
}
