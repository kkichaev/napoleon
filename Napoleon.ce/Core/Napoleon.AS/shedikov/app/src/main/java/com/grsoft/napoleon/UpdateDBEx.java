package com.grsoft.napoleon;

import java.util.List;

import android.widget.CheckBox;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentPda;
import com.grsoft.dataobjects.Forvarder;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		
		CheckBox cb = (CheckBox)findViewById(R.id.cbCost);
		cb.setChecked(true);
		
		cb = (CheckBox) findViewById(R.id.cbDebt);
		cb.setChecked(true);
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> result = super.getGenDataHitchings();
		
		result.add(new RcvNewHitching(Forvarder.class, "Forvarder"));
		result.add(new RcvNewHitching(AgentPda.class, "AgentPda"));
		
		return result;
	}
}
