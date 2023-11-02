package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.RestHitching;
import com.grsoft.dataobjects.CustomCost;
import com.grsoft.dataobjects.OrgMatrix;
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
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(0, new RcvNewHitching(CustomCost.class, "CustomCost"));
		ret.add(new RcvNewHitching(OrgMatrix.class, "OrgMatrix"));
		ret.add(new RestHitching());
		return ret;
	}
}
