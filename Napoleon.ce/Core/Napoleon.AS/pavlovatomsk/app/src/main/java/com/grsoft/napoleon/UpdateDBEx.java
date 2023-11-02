package com.grsoft.napoleon;

import com.grsoft.network.NetworkAsyncTask;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected boolean onFinishUpdate(NetworkAsyncTask task) {
		PriceCash.load();
		return super.onFinishUpdate(task);
	}
}
