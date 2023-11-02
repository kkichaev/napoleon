package com.grsoft.napoleon.util;

import com.grsoft.napoleon.WarehouseEx;
import com.grsoft.util.BuildSetThread;
import com.grsoft.util.WarehouseAdapter;


public class BuildSetThreadEx extends BuildSetThread {

	public BuildSetThreadEx(WarehouseAdapter adapter) {
		super(adapter);
	}

	@Override
	protected Integer doInBackground(Void... params) {
		int result =  super.doInBackground(params);
		((WarehouseEx)adapter.warehouse).overview();
		return result;
	}
}
