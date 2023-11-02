package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.CheckBox;

import com.grsoft.database.Hitching;
import com.grsoft.database.PriceRcvr;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.Discount;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.Plan;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		
		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		cbRemains.setChecked(false);
		cbRemains.setVisibility(View.INVISIBLE);
	}
	
	@Override
	protected Hitching getPriceHitching(boolean rcvRemains) { return new PriceRcvr(); }
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> res =  super.getGenDataHitchings();
		if( res.size() > 0 ) {
			res.add(0, new RcvNewHitching(Discount.class, "Discount"));
	
			res.add(new RcvNewHitching(AgentPrefix.class, "AgentPrefix"));
			res.add(new RcvNewHitching(Plan.class, "Plan"));
		}
		return res;
	}
	
	@Override
	protected List<Hitching> getDebetHitching() {
		ArrayList<Hitching> debtHitchings = new ArrayList<Hitching>();
		debtHitchings.add(new RcvNewHitching(DbObject.getDataType(Payment.class),"Payment"));
		return debtHitchings;
	}
}
