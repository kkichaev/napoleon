package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.PrezentHitching;
import com.grsoft.database.StoreUtils;
import com.grsoft.network.NetworkAsyncTask;
import android.os.Bundle;
import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		((CheckBox)findViewById(R.id.cbVisit)).setChecked(true);
		((CheckBox)findViewById(R.id.cbPresent)).setChecked(true);
		((CheckBox)findViewById(R.id.cbDebt)).setChecked(true);
		((CheckBox)findViewById(R.id.cbRemains)).setChecked(false);
	}
	
	@Override
	protected List<Hitching> getPrezentHitching() {
		List<Hitching> ret = new ArrayList<Hitching>();
		ret.add(new PrezentHitching());
		return ret;
	}
	
	@Override
	protected boolean onFinishUpdate(NetworkAsyncTask task) {
		if(((CheckBox)findViewById(R.id.cbPresent)).isChecked())
			StoreUtils.commitCRCChanges();
		return super.onFinishUpdate(task);
	}
}
