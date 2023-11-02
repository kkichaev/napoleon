package com.grsoft.napoleon;

import java.util.List;

import android.os.Bundle;
import android.widget.CheckBox;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.RealizationHitching;
import com.grsoft.dataobjects.CommonMatrix;
import com.grsoft.dataobjects.OrgMtx;
import com.grsoft.napoleon.documents.RealizationDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDB2Ex extends UpdateDBEx {
	CheckBox cbRealization;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cbRealization = (CheckBox) findViewById(R.id.cbRealization);
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> result = super.getGenDataHitchings();
		result.add(new RcvNewHitching(CommonMatrix.class, "CommonMatrix"));
		result.add(new RcvNewHitching(OrgMtx.class, "OrgMtx"));
		
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
	
	@Override
	protected List<Hitching> getRestoreHitching() {
		List<Hitching> result = super.getRestoreHitching();
		result.add(new DocumentRestore(WSOrderDoc.instance()));
		return result;
	}
}
