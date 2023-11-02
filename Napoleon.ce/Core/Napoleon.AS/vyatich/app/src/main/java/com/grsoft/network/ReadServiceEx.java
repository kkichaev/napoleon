package com.grsoft.network;

import java.util.List;

import android.content.Context;

import com.grsoft.database.Hitching;
import com.grsoft.database.PODHitching;
import com.grsoft.database.PODHitchingEx;

public class ReadServiceEx extends ReadService {

	public ReadServiceEx(List<Hitching> hitchings) {
		super(hitchings);
	}
	
	public ReadServiceEx(List<Hitching> hitchings, boolean readAsManager, Context ctx) {
		super(hitchings, readAsManager, ctx);
	}
	
	@Override
	protected PODHitching createPODHitching() {
		return new PODHitchingEx();
	}
}
