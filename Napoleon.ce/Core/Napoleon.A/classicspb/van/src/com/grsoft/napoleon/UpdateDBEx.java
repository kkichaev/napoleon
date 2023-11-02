package com.grsoft.napoleon;

import java.util.List;

import android.view.View;
import android.widget.CheckBox;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.OrgTask;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDBPrint {
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
		cbRemains.setChecked(false);
		cbRemains.setVisibility(View.GONE);
	}
}
