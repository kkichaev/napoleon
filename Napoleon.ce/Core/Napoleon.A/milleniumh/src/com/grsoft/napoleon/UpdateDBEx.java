package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DistrPrice;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDBPrint {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new RcvNewHitching(DistrPrice.class, "DistrPrice"));
		return ret;
	}
}
