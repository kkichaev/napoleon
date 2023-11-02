package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.DymovTaskSender;
import com.grsoft.database.Hitching;
import com.grsoft.database.PriceColorHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DymovTask;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new PriceColorHitching());
		ret.add(new RcvNewHitching(DymovTask.class, "DymovTask"));
		return ret;
	}
	
	@Override
	public List<ObjectListener> getExported() {
		List<ObjectListener> ret = super.getExported();
		ret.add(new DymovTaskSender());
		return ret;
	}
}
