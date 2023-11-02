package com.grsoft.napoleon;

import java.util.List;

import android.widget.CheckBox;

import com.grsoft.database.Hitching;
import com.grsoft.napoleon.plans.dataobjects.Plan;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> list = super.getGenDataHitchings();
		
		if(((CheckBox)findViewById(R.id.cbGenData)).isChecked()) {
			list.add(new Hitching(Plan.class, "Plan"));
		}
		
		return list;
	}
}
