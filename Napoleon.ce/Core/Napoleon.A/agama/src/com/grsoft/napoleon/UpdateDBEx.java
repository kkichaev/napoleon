package com.grsoft.napoleon;

import com.grsoft.database.Hitching;
import com.grsoft.database.PriceHitchingW;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.exception.RuntimeException;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	
	static final String PRICE_OBJECT = "PriceAnd";
	
	@Override
	protected Hitching getPriceHitching(boolean rcvRemains) {
		PriceHitchingW result = new PriceHitchingW(PRICE_OBJECT);
		result.setPriceFilter(rcvRemains);
		return result;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		cbRemains.setChecked(false);
		cbRemains.setVisibility(View.GONE);
	}
	
	@Override
	protected boolean onFinishUpdate(NetworkAsyncTask task) {
		try {
			DeliveryDoc.instance().refreshDocSum();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
