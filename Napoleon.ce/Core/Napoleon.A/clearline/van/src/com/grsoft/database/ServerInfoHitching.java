package com.grsoft.database;

import com.grsoft.dataobjects.ServerInfoObject;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import android.os.SystemClock;

public class ServerInfoHitching extends Hitching {
	
	public ServerInfoHitching() {
		super(ServerInfoObject.class, "%ServerInfo");
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		ServerInfoObject obj = (ServerInfoObject) rawObject.createDataObject(dataObject);
		obj.elapsedTime = SystemClock.elapsedRealtime();
		dbProxy.insertRecord(obj);
	}
}
