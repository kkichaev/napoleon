package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.PaysHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Plan;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new RcvNewHitching(Plan.class, "Plans"));
		return ret;
	}
	
	@Override
	protected List<Hitching> getDebetHitching() {
		List<Hitching>  ret = super.getDebetHitching();
		
		ret.add(new PaysHitching());
		return ret;
	}
}
