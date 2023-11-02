package com.grsoft.database;

import com.grsoft.dataobjects.ServerAnswer;
import com.grsoft.network.LoginData;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import android.content.Context;

public class ManagerHitching extends LoginHitching {
	Context ctx;
	public ManagerHitching(Context ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		ServerAnswer serverAnswer = (ServerAnswer) rawObject.createDataObject(ServerAnswer.class);
		
		isOK = (serverAnswer.response != 0);
		if( isOK )
			LoginData.putDuration(serverAnswer, ctx);
		else
			message = serverAnswer.message;
	}
	
}
