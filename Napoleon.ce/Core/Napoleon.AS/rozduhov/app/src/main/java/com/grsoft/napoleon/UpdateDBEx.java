package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.CheckBox;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.database.PriceHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.FMLM;
import com.grsoft.dataobjects.FirmRozduhov;
import com.grsoft.dataobjects.MLM;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PricePhotoHitchingEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		cbRemains.setChecked(false);
		cbRemains.setVisibility(View.GONE);
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new RcvNewHitching(FirmRozduhov.class, "Firm"));
		ret.add(new RcvNewHitching(MLM.class, "MLM"));
		ret.add(new RcvNewHitching(FMLM.class, "FMLM"));
		return ret;
	}
	
	@Override
	protected List<Hitching> getPrezentHitching() { 
		List<Hitching> result = new ArrayList<Hitching>();
		result.add(new PricePhotoHitchingEx());
		return result; 
	}
	
	@Override
	protected Hitching getPriceHitching(boolean rcvRemains) {
		return new PriceHitchingEx();
	}
}

class PriceHitchingEx extends PriceHitching {
	@Override
	public void prepareReading() {
		try {
			DbWriter.checkDBTable(DbObject.getDataType(Price.class));
			String tableName = DataObjectInfo.getInstance().getTableName(Price.class);
			String stmt = "update " + tableName + " set qty = 0, qtys = null";
			DataBaseManager.getDataBase().execSQL(stmt);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}