package com.grsoft.napoleon;

import java.util.List;
import com.grsoft.database.Hitching;
import com.grsoft.database.OrderDecisionHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.MetelicaPrices;
import com.grsoft.dataobjects.OrgTaskM;
import com.grsoft.dataobjects.Restin;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		
		CostStrategyEx.resetCache();
		
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new RcvNewHitching(Restin.class, "RestIn"));
		ret.add(new Hitching(OrgTaskM.class, "OrgTask"));
		ret.add(new OrderDecisionHitching(this));
		ret.add(new RcvNewHitching(MetelicaPrices.class, "Prices"));
		return ret;
	}
}
