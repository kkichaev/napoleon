package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.AgentRouteHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.ManagerRouteHitching;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.exception.RuntimeException;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		cbRemains.setChecked(false);
		cbRemains.setVisibility(View.GONE);
	}
	
	@Override
	public List<ObjectListener> getExported() {
		List<ObjectListener> result = super.getExported(); 
		
		if(result == null)
			result = new ArrayList<ObjectListener>();
		
		result.add(new AgentRouteHitching());
		
		return result;
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> result =  super.getGenDataHitchings();
		
		if(result == null)
			result = new ArrayList<Hitching>();
		
		result.add(new ManagerRouteHitching());
		
		return result;
	}
}
