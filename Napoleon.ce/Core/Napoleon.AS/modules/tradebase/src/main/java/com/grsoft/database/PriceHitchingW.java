package com.grsoft.database;

import android.annotation.SuppressLint;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceQtyItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.Features;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class PriceHitchingW extends HitchOnSelect {

	public PriceHitchingW() {
		this("Price");
	}

	public PriceHitchingW(String objectName) {
		super(DbObject.getDataType(Price.class), objectName);
	}
	
	public void setPriceFilter(boolean rcvRemainPrice) {
		if( rcvRemainPrice == false )
			setCondition("SetQtyFilter(False)");
	}
	
	@Override
	public void prepareReading() {
		try {
			DbWriter.checkDBTable(DbObject.getDataType(Price.class));
			String tableName = DataObjectInfo.getInstance().getTableName(Price.class);
			String stmt = "update " + tableName + " set qty = 0";
			
			if( Features.PRINT_MODULE )
				stmt += ", vanQty = 0";
			DataBaseManager.getDataBase().execSQL(stmt);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressLint("DefaultLocale")
	protected void beforeInsert(Price dobj) {
		dobj.srchName = dobj.name.toUpperCase();
	}

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Price dobj = (Price)rawObject.createDataObject(dataObject);
		beforeInsert(dobj);

		if (Features.WH_QTY){
			dobj.updateWhState();
		}

		dbProxy.insertRecord(dobj);
	}
}
