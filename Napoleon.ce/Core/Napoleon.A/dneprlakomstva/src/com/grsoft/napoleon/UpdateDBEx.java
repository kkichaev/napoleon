package com.grsoft.napoleon;

import java.util.List;

import android.view.View;
import android.widget.CheckBox;

import com.grsoft.database.DlvMoveHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.RemnantsHitching;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		
		((CheckBox) findViewById(R.id.cbDebt)).setChecked(true);
		findViewById(R.id.cbRemains).setVisibility(View.GONE);
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new RemnantsHitching());
		return ret;
	}
	
	@Override
	protected void postSync(Boolean result) {
		if(result)
			try {
//				DebtDoc.instance().refreshDocSum();
				CostStrategyEx.clearCache();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	@Override
	protected List<Hitching> getDebetHitching() {
		List<Hitching> ret = super.getDebetHitching();
		ret.add(new DlvMoveHitching());
		return ret;
	}
}
