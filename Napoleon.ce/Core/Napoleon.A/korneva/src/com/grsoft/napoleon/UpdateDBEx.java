package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.OrdFlag;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.exception.RuntimeException;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	private static final String CB_REMAINS = "cb_remains";

	@Override
	protected void onPause() {
		super.onPause();

		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		SharedPreferences p = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor ed = p.edit();
		ed.putBoolean(CB_REMAINS, cbRemains.isChecked());
		ed.commit();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		SharedPreferences p = getPreferences(Context.MODE_PRIVATE);
		cbRemains.setChecked(p.getBoolean(CB_REMAINS, Features.LOAD_FULL_PRICE));
	}
	
	@Override
	protected boolean onFinishUpdate(NetworkAsyncTask task) {
		CheckBox cbClearDB = (CheckBox) findViewById(R.id.cbClearDB);
		
		if(cbClearDB.isChecked()){
			((NapoleonApp)getApplication()).markUpdatePresentTime(-1);
		}
			
		return super.onFinishUpdate(task);
	}

	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> result = super.getGenDataHitchings();
		result.add(new RcvNewHitching(DbObject.getDataType(OrdFlag.class), "OrdFlag"));
		return result;
	}
}
