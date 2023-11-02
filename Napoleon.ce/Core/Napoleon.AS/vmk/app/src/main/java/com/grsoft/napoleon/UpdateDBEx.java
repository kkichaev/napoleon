package com.grsoft.napoleon;

import java.util.Date;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected void postSync(Boolean result) {
		super.postSync(result);
		
		if (result)
			MainEx.loadSet(new Date());
	}
	
	
}
