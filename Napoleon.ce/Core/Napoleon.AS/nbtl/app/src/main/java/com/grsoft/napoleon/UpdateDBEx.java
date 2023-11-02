package com.grsoft.napoleon;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import java.util.List;

import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.database.OrgHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.ContractDef;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;


public class UpdateDBEx extends UpdateDBW {
	CheckBox cbContract;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.cbRemains).setVisibility(View.GONE);
		cbContract = (CheckBox)findViewById(R.id.cbContract);
	}
	
	@Override
	protected Hitching getPriceHitching(boolean rcvRemains) {
		return new RcvNewHitching(Price.class, "Price"){
			@SuppressLint("DefaultLocale")
			protected void beforeInsert(Price dobj) {
				dobj.srchName = dobj.name.toUpperCase();
			}
			@Override
			public void onRead(RawObject rawObject) throws RuntimeException {
				Price dobj = (Price)rawObject.createDataObject(dataObject);
				beforeInsert(dobj);
				dbProxy.insertRecord(dobj);
			}
		};
	}
	
	@Override
	protected int getContentView() {
		return R.layout.updatedbex;
	}
	
	@Override
	protected Hitching getOrgHitching() {
		return new OrgHitching(){
			@Override
			public void prepareReading() {
				DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(dataObject));
			}
		};
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> res = super.getGenDataHitchings();
		
		if (cbContract.isChecked())
			res.add(new RcvNewHitching(ContractDef.class));
		
		return res;
	}
}
