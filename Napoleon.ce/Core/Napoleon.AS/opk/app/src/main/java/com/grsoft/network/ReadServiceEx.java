package com.grsoft.network;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.MessageHitching;
import com.grsoft.database.MessageHitchingEx;

public class ReadServiceEx extends ReadService {

	public ReadServiceEx(List<Hitching> hitchings) {
		super(hitchings);
	}

	@Override
	protected MessageHitching createMessageHitching() {
		return new MessageHitchingEx();
	}
}
