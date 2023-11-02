package com.grsoft.database;

import com.grsoft.dataobjects.CommonIncass;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Consts;

import android.util.Log;

public class CommonIncassRestore extends DataObjectRestore {
	public CommonIncassRestore() {
		super(CommonIncass.class, "CommonIncass", "created");
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Log.d(Consts.D_TAG, "DataObjectRestore.onRead");
		DataObject dobj = rawObject.createDataObject(dataObject);
		beforeWrite(dobj);
		((CommonIncass)dobj).params |= ParamState.ofExported;
		dbProxy.insertRecord(dobj);
	}
}
