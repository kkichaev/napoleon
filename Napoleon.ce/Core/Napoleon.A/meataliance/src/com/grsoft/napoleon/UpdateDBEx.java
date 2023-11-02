package com.grsoft.napoleon;

import java.util.List;
import android.os.Bundle;
import android.widget.CheckBox;
import com.grsoft.database.Hitching;
import com.grsoft.database.RealizationHitching;
import com.grsoft.napoleon.documents.RealizationDoc;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	CheckBox cbRealization;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cbRealization = (CheckBox) findViewById(R.id.cbRealization);
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> result = super.getGenDataHitchings();
		
		if(cbRealization.isChecked())
			result.add(new RealizationHitching());
		
		return result;
	}
	
	@Override
	protected int getContentView() {
		return R.layout.updatedbex;
	}
	
	@Override
	protected boolean onFinishUpdate(NetworkAsyncTask task) {
		try {
			RealizationDoc.instance().refreshDocSum();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
