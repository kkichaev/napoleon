package com.grsoft.napoleon;

import com.grsoft.napoleon.util.DeliveryList;
import com.grsoft.network.NetworkAsyncTask;

import android.view.View;
import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		cbRemains.setChecked(false);
		cbRemains.setVisibility(View.GONE);
	}
	
	@Override
	protected boolean onFinishUpdate(NetworkAsyncTask task) {
		DeliveryList.clear();
		return super.onFinishUpdate(task);
	}
}
