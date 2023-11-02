package com.grsoft.napoleon;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.grsoft.database.FullPrice;
import com.grsoft.database.Hitching;
import com.grsoft.database.PriceHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DeliveryMan;
import com.grsoft.dataobjects.Discount;
import com.grsoft.dataobjects.Plan;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDBPrint{
	private static final String OPEN = "open";
	
	@Override
	protected Hitching getPriceHitching(boolean rcvRemains) {
		return rcvRemains ? new PriceHitching() {
			@Override
			public void onRead(RawObject rawObject) throws RuntimeException {
				PriceEx dobj = (PriceEx) rawObject.createDataObject(dataObject);
				dobj.srchName = dobj.name.toUpperCase() + "|" + dobj.barcode.toUpperCase();
				dbProxy.insertRecord(dobj);
			}
		} : new FullPrice() {
			@Override
			public void onRead(RawObject rawObject) throws RuntimeException {
				PriceEx dobj = (PriceEx) rawObject.createDataObject(dataObject);
				dobj.srchName = dobj.name.toUpperCase() + "|" + dobj.barcode.toUpperCase();
				dbProxy.insertRecord(dobj);
			}
		};
	};
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> result = super.getGenDataHitchings();
		result.add(new Hitching(DeliveryMan.class, "DeliveryMan"));
		result.add(new Hitching(Discount.class, "Discount"));
		result.add(new RcvNewHitching(Plan.class, "Plan"));
		return result;
	}
	
	static void openActivity(Context context) {
		Intent i = new Intent(context, activity);
		i.putExtra(OPEN, true);
		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.cbRemains).setVisibility(View.VISIBLE);
		
		if(!getIntent().getBooleanExtra(OPEN, false)){
			((CheckBox) findViewById(R.id.cbVisit)).setChecked(true);
			((CheckBox) findViewById(R.id.cbPresent)).setChecked(true);
			((CheckBox) findViewById(R.id.cbDebt)).setChecked(true);
			((Button) findViewById(R.id.btnUpdate)).performClick();
		}
	}
}
