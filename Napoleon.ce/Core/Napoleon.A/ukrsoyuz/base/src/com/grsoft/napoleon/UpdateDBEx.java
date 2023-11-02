package com.grsoft.napoleon;

import java.util.List;

import android.os.Bundle;
import android.widget.CheckBox;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.PriceItemColor;
import com.grsoft.dataobjects.PriceSetQty;
import com.grsoft.dataobjects.ReturnData;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		cbRemains.setChecked(false);
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> result = super.getGenDataHitchings();
		
		result.add(new RcvNewHitching(ReturnData.class, "ReturnData"));
		result.add(new RcvNewHitching(PriceSetQty.class));
		result.add(new RcvNewHitching(PriceItemColor.class));
		
		PriceItemColor.resetCach();
		
		return result;
	}
}
