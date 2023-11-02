package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.PlanHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Agent;
import com.grsoft.dataobjects.OrgDiscount;
import com.grsoft.dataobjects.Region;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new RcvNewHitching(Region.class, "Regions"));
		ret.add(new RcvNewHitching(Agent.class, "Agents"));
		ret.add(new RcvNewHitching(OrgDiscount.class, "OrgDiscount"));
		ret.add(new PlanHitching());
		
		return ret;
	}
}
