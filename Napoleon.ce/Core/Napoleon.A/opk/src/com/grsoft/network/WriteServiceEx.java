package com.grsoft.network;

import java.util.List;

import com.grsoft.database.MessageHitching;
import com.grsoft.database.MessageHitchingEx;

public class WriteServiceEx extends WriteService {

	public WriteServiceEx(List<? extends ObjectListener> objectsToSend,
			boolean rcvRemnants) {
		super(objectsToSend, rcvRemnants);
		}
	
	@Override
	protected MessageHitching createMessageHitching() {
		return new MessageHitchingEx();
	}
}
