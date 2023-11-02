package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new AgentPlanRcv());
		return ret;
	}
}

class AgentPlanRcv extends RcvNewHitching {
	public AgentPlanRcv () {
		super(AgentPlan.class, "AgentPlan");
		selectCMD = "SELECT";
	}
	
	@Override
	public String getParams() throws RuntimeException {
		SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM.yyyy");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -6);
		c.set(Calendar.DAY_OF_MONTH, 1);
		String filter = String.format(" \"userid\" = '$CURRENT_USERID' and \"begin\" >= ToDate('%s')",
				simpleDateFormat.format(c.getTime()));
		return objectName + ":" + filter;
	}
}
