package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.OrgGroup;
import com.grsoft.dataobjects.TypeOrgMatrix;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> result = super.getGenDataHitchings();
		
		if(result == null)
			new ArrayList<Hitching>();
		
		result.add(new RcvNewHitching(OrgGroup.class, "OrgGroup"));
		result.add(new RcvNewHitching(TypeOrgMatrix.class, "TypeOrgMatrix"));

		return result;
	}
}
