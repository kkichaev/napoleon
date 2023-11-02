package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.OrgTask;

import android.view.View;
import android.widget.CheckBox;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBBase extends UpdateDB {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new RcvNewHitching(OrgMatrix.class, "OrgMatrix"));
		ret.add(new Hitching(OrgTask.class, "OrgTask"));
		return ret;
	}
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		cbRemains.setVisibility(View.GONE);
	}
}
