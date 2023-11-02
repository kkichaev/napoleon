package com.grsoft.database;

import com.grsoft.dataobjects.OrderProceeded;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import android.database.sqlite.SQLiteStatement;

public class RestoreDocProceeded extends DataObjectRestore {
	protected ProceededDocHandler handler = new ProceededDocHandler();

	public RestoreDocProceeded() {
		super(OrderProceeded.class, "ArchiveOrderProceeded", "created");
	}

	protected RestoreDocProceeded(Class<? extends OrderProceeded> docClass, String objectName, String timeField) {
		super(docClass, objectName, timeField);
	}

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		OrderProceeded dobj = (OrderProceeded) rawObject.createDataObject(dataObject);
		handler.handle(dobj, ParamState.ofProceeded | ParamState.ofExported);
	}
	
	@Override
	public void onEnd() {
		handler.clear();
		super.onEnd();
	}
}
