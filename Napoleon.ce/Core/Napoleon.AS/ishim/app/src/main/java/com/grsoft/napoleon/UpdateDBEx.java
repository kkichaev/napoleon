package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.network.exception.RuntimeException;

import android.os.Bundle;
import android.view.View;

public class UpdateDBEx extends UpdateDB {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.cbRemains).setVisibility(View.GONE);
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		
		((CostStrategyEx)CostStrategy.defaultInstance).clearCache();
		
		List<Hitching> ret = super.getGenDataHitchings();
//		ret.add(new PriceFolderHitching());
		return ret;
	}
}
