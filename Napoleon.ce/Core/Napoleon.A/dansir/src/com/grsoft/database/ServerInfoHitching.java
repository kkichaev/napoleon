package com.grsoft.database;

import java.util.Date;

import com.grsoft.dataobjects.ServerInfoObject;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class ServerInfoHitching extends Hitching {
	
	public Date serverDate = new Date(); 
	
	public ServerInfoHitching() {
		super(ServerInfoObject.class, "%ServerInfo");
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		ServerInfoObject obj = (ServerInfoObject) rawObject.createDataObject(dataObject);
		serverDate = obj.curdate;
	}
}
