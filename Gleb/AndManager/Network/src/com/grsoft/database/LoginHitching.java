package com.grsoft.database;

import com.grsoft.dataobjects.ServerAnswer;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class LoginHitching extends Hitching {

	protected boolean isOK = false;
	protected String message = "";
	
	public LoginHitching() { super(ServerAnswer.class, "ServerAnswer"); }
	
	public boolean isOK() { return isOK; }
	public String getMessage() { return message; }
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		ServerAnswer serverAnswer = (ServerAnswer) rawObject.createDataObject(ServerAnswer.class);
		
		isOK = (serverAnswer.response != 0);
		message = serverAnswer.message;
		
//		final int LOGIN_FAILURE = 0;
//		if (serverAnswer.response == LOGIN_FAILURE)
//			throw new RuntimeException(new LoginFailure(serverAnswer));
	}

}
