package com.grsoft.database;

import java.util.Calendar;
import com.grsoft.dataobjects.MessageNew;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class MessageHitchingNew extends Hitching {

	public MessageHitchingNew() {
		super(MessageNew.class);
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		MessageNew dobj = (MessageNew) rawObject.createDataObject(dataObject);
		dobj.date = Calendar.getInstance().getTime();
		dbProxy.insertRecord(dobj);
	}
}
