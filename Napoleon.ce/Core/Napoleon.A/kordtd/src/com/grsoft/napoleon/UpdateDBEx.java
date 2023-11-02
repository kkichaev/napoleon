package com.grsoft.napoleon;

import com.grsoft.database.Hitching;
import com.grsoft.database.OrgHitchingEx;

public class UpdateDBEx extends UpdateDB {
	@Override protected Hitching getOrgHitching() { return new OrgHitchingEx(); }
}
