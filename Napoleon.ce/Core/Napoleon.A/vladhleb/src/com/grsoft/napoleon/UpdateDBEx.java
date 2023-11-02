package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.PrezentData;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected List<Hitching> getPrezentHitching() {
		List<Hitching> result = super.getPrezentHitching();
		result.add(new RcvNewHitching(PrezentData.class, "PrezentData"));
		return result;
	}
}
