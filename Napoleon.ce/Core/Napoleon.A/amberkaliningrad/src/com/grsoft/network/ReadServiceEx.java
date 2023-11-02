package com.grsoft.network;

import java.util.List;

import com.grsoft.database.Hitching;

public class ReadServiceEx extends ReadService {

	public ReadServiceEx(List<Hitching> hitchings) {
		super(hitchings);
		
		recieveHitch.add(new OrgStopHitching());
	}

}
