package com.grsoft.napoleon;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grsoft.database.DbReader;
import com.grsoft.database.Hitching;
import com.grsoft.database.OrgHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.OrgLocation;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.FolderTree;

import android.os.Bundle;
import android.widget.CheckBox;


public class UpdateDB extends UpdateDBW {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CheckBox cb = (CheckBox) findViewById(R.id.cbVisit);
		cb.setChecked(true);
	}
	
	@Override protected Hitching getOrgHitching() {
		CostStrategy.refreshCash();
		return new OrgHitching();
	};
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> res = super.getGenDataHitchings();
		res.add(new RcvNewHitching(OrgLocation.class));
		return res;
	}
}
