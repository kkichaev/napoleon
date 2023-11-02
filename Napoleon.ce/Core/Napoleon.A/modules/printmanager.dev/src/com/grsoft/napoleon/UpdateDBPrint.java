package com.grsoft.napoleon;

import java.util.List;

import android.view.View;
import android.widget.CheckBox;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.SalesRestore;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBPrint extends UpdateDB {
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		cbRemains.setChecked(false);
		cbRemains.setVisibility(View.INVISIBLE);
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> result = super.getGenDataHitchings();
		
		if(((CheckBox)findViewById(R.id.cbGenData)).isChecked()){ 
			result.add(new RcvNewHitching(DbObject.getDataType(Firm.class), "Firm"));
			result.add(new RcvNewHitching(AgentPrefix.class, "AgentPrefix"));
		}
		
		return result;
	}
	
	@Override
	protected List<Hitching> getRestoreHitching() {
		List<Hitching> result = super.getRestoreHitching();
		result.add(new SalesRestore());
		result.add(new DocumentRestore(PkoDoc.instance()));
		return result;
	}
}
