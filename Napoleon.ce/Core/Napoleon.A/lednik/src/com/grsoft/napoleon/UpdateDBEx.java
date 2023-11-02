package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.CustomCost;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(0, new RcvNewHitching(CustomCost.class, "CustomCost"));
		ret.add(new RcvNewHitching(OrgMatrix.class, "OrgMatrix"));
		return ret;
	}
}
