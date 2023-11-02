package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.AgentRouteHitching;
import com.grsoft.network.ObjectListener;

public class UpdateDBEx extends UpdateDB {
	@Override
	public List<ObjectListener> getExported() {
		List<ObjectListener> result = super.getExported(); 
		
		if(result == null)
			result = new ArrayList<ObjectListener>();
		
		result.add(new AgentRouteHitching());
		
		return result;
	}
}
