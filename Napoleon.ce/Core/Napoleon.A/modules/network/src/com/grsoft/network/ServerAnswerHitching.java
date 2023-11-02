package com.grsoft.network;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.ServerAnswer;
import com.grsoft.network.exception.RuntimeException;

public class ServerAnswerHitching extends Hitching {
	Integer response = null;
	String message = "";
	
	List<? extends ObjectListener> objects;
	
	public ServerAnswerHitching() {
		super(ServerAnswer.class, "ServerAnswer");
	}
	
	public void setObjects(List<? extends ObjectListener> objects) {
		this.objects = objects;
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		ServerAnswer sa = (ServerAnswer) rawObject.createDataObject(ServerAnswer.class);
		if(response == null)
			response = sa.response;
		
		if (sa.response == 1 ) {			
			if( objects != null) {
				for(ObjectListener ol : objects ) {
					if( ol.getObjectName().compareTo(sa.message) == 0 )
						ol.onEnd();
				}
			}
		} else {
			if( response == 1 ){
				message = sa.message;
				response = 0;
			}else
				message += "<br>" + sa.message;
		}
	}
	
	public boolean IsOK() {
		return response == 1;
	}
	
	public String getMessage() { return message; }
}
