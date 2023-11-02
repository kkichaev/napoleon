package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.OrderRestore;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB{
	@Override
	protected void postSync(Boolean result) {
		if(result)
			NapoleonEx.debtOrgs = null;
	}
	
	@Override
	public DocumentRestore createOrderRestore() {
		return new OrderRestore();
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> result = super.getGenDataHitchings();
		
		if(result != null)
			result.add(new RcvNewHitching(AgentPrefix.class, "AgentPrefix"));
		
		return result;

	}
}
