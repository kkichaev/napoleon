package com.grsoft.ads;

import java.util.concurrent.locks.Lock;

import com.grsoft.ads.utils.LockOwner;
import com.grsoft.util.GlobalServiceContext;
import com.grsoft.view.RegDurationActivity;

public class UpdateActivity extends RegDurationActivity 
implements LockOwner{

	@Override
	public Lock getLock() {
		return ((AdsService)GlobalServiceContext.service).getLock();
	}
}
