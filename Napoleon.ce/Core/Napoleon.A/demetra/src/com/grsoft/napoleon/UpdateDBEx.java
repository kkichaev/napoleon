package com.grsoft.napoleon;

import java.util.List;

import android.os.Bundle;
import android.widget.CheckBox;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DistribGroup;
import com.grsoft.dataobjects.OrgGroup;
import com.grsoft.dataobjects.TypeOrgMatrix;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((CheckBox) findViewById(R.id.cbVisit)).setChecked(true);
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> result = super.getGenDataHitchings();
		
		result.add(new RcvNewHitching(OrgGroup.class, "OrgGroup"));
		result.add(new RcvNewHitching(TypeOrgMatrix.class, "TypeOrgMatrix"));
		result.add(new RcvNewHitching(DistribGroup.class, "DistribGroup"));
		
		return result;
	}
}
