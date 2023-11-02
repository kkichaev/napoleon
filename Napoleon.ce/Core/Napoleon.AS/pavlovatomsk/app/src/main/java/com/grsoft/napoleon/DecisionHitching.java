package com.grsoft.napoleon;

import com.grsoft.database.DataObjectRestore;
import com.grsoft.dataobjects.Decision;

public class DecisionHitching extends DataObjectRestore{

	public DecisionHitching() {
		super(Decision.class, "Decision", "created");
	}

}
