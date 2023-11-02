package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.network.exception.RuntimeException;

import android.view.View;
import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();

		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		cbRemains.setChecked(false);
		cbRemains.setVisibility(View.GONE);
		
		((CheckBox) findViewById(R.id.cbDebt)).setChecked(true);
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new RcvNewHitching(OrgMatrix.class, "OrgMatrix"));
		return ret;
	}
}
