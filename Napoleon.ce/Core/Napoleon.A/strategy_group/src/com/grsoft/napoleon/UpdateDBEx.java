package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.ExecAgent;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new RcvNewHitching(ExecAgent.class, "ExecAgent"));
		
		((CostStrategyEx)CostStrategy.defaultInstance).clearCache();
		return ret;
	}
}
