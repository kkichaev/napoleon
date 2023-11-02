package com.grsoft.network;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.PODHitching;
import com.grsoft.database.PODHitchingEx;

public class ReadServiceEx extends ReadService {

	public ReadServiceEx(List<Hitching> hitchings) {
		super(hitchings);
	}

	@Override protected PODHitching createPODHitching() { return new PODHitchingEx(); }
}
