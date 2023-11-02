package com.grsoft.database;

import android.content.Context;

import com.grsoft.dataobjects.ServerAnswer;
import com.grsoft.network.LoginData;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class LoginHitching extends Hitching {

	protected boolean isOK = false;
	protected String message = "";
	protected String kind = "";
	private String duration = null; 
	
	public LoginHitching() { super(ServerAnswer.class, "ServerAnswer"); }
	
	public boolean isOK() { return isOK; }
	public String getMessage() { return message; }
	public String getKind() { return kind; }
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		ServerAnswer serverAnswer = (ServerAnswer) rawObject.createDataObject(ServerAnswer.class);
		
		isOK = (serverAnswer.response != 0);
		message = serverAnswer.message;
		kind = serverAnswer.kind;
		
		if(duration == null)
			duration = message;
		
//		final int LOGIN_FAILURE = 0;
//		if (serverAnswer.response == LOGIN_FAILURE)
//			throw new RuntimeException(new LoginFailure(serverAnswer));
	}
	
	public void saveDuration(Context ctx){
		ServerAnswer sa = new ServerAnswer();
		sa.message = duration;
		LoginData.putDuration(sa, ctx);
	}
	
}
