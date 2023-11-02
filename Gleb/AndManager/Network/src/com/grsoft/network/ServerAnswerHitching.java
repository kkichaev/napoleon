package com.grsoft.network;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.ServerAnswer;
import com.grsoft.network.exception.RuntimeException;

public class ServerAnswerHitching extends Hitching {
	private ServerAnswer serverAnswer;
	
	List<? extends ObjectListener> objects;
	
	public ServerAnswerHitching() {
		super(ServerAnswer.class, "ServerAnswer");
	}
	
	public void setObjects(List<? extends ObjectListener> objects) {
		this.objects = objects;
	}
	
	@Override public void onStart() { serverAnswer = null; }
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		serverAnswer = (ServerAnswer) rawObject.createDataObject(ServerAnswer.class);
		
		if (serverAnswer.response == 1 && objects != null) {
			for(ObjectListener ol : objects ) {
				if( ol.getObjectName().compareTo(serverAnswer.message) == 0 )
					ol.onEnd();
			}
		}
	}
	
	public boolean IsOK() {
		return serverAnswer != null && serverAnswer.response == 1;
	}
	
	public ServerAnswer getServerAnsver() {
		return serverAnswer;
	}
	
	public String getMessage() { return (serverAnswer == null) ? "" : serverAnswer.message; }
}
