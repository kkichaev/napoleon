package com.grsoft.database;

import com.grsoft.dataobjects.Pays;
import com.grsoft.napoleon.documents.PaysDoc;
import com.grsoft.network.exception.RuntimeException;

public class PaysHitching extends RcvNewHitching {
	public PaysHitching() {
		super(Pays.class, "Pays");
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		
		try {
			PaysDoc.instance().refreshDocSum();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
}
