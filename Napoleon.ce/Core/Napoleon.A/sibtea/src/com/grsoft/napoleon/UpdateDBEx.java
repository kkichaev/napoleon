package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Arrival;
import com.grsoft.dataobjects.OrgFolderDiscount;
import com.grsoft.dataobjects.OrgItemDiscount;
import com.grsoft.dataobjects.PlanReport;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {

	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> res = super.getGenDataHitchings();
		res.add(new RcvNewHitching(OrgFolderDiscount.class, "OrgFDiscount"));
		res.add(new RcvNewHitching(OrgItemDiscount.class, "OrgPDiscount"));
		res.add(new RcvNewHitching(Arrival.class, "Arrival"));
		res.add(new RcvNewHitching(PlanReport.class, "PlanReport"));
		return res;
	}
}
