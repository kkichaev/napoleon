package com.grsoft.network;

import java.util.List;

import com.grsoft.database.PODHitching;
import com.grsoft.database.PODHitchingEx;

public class WriteServiceEx extends WriteService {

	public WriteServiceEx(List<? extends ObjectListener> objectsToSend, boolean rcvRemnants) {
		super(objectsToSend, rcvRemnants);
	}

	@Override protected PODHitching createPODHitching() { return new PODHitchingEx(); }
}
