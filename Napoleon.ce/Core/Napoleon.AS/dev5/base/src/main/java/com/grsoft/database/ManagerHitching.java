package com.grsoft.database;
import com.grsoft.aceteam.R;

import android.content.Context;

import com.grsoft.dataobjects.ServerAnswer;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class ManagerHitching extends LoginHitching {
	Context ctx;
	public ManagerHitching(Context ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		ServerAnswer serverAnswer = (ServerAnswer) rawObject.createDataObject(ServerAnswer.class);
		
		isOK = (serverAnswer.response != 0);
		if( !isOK )
			message = serverAnswer.message;

//		if( isOK )
//			LoginData.putDuration(serverAnswer, ctx);
//		else
//			message = serverAnswer.message;
	}
	
}
