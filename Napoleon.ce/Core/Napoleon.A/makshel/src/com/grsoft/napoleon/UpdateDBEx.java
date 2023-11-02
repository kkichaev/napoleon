package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.PrezentHitching;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected List<Hitching> getPrezentHitching() {
		List<Hitching> ret = new ArrayList<Hitching>();
		ret.add(new PrezentHitching());
		return ret;
	}
}
