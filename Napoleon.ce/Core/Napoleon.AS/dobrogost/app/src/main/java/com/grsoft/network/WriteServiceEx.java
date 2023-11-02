package com.grsoft.network;

import java.util.List;
import com.grsoft.database.AgentActivityHitching;

public class WriteServiceEx extends WriteService {

	public WriteServiceEx(List<? extends ObjectListener> objectsToSend, boolean rcvRemnants) {
		super(objectsToSend, rcvRemnants);
		
		boolean haveAgents = false;
		if(objectsToSend !=null) {
			for(ObjectListener ol : objectsToSend)
				if(ol instanceof AgentActivityHitching ) {
					haveAgents = true;
					break;
				}
		}

		if(!haveAgents)
			this.objectsToSend.add(new AgentActivityHitching());
	}
}