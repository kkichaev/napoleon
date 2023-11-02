package com.grsoft.ads.database;

import android.util.Log;

import com.grsoft.ads.dataobjects.Client;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class ClientHitching extends Hitching {
    private static final String TAG = "ClientHitching";
    
	public ClientHitching() {
		super(Client.class, "Client");
		DbWriter.checkDBTable(DbObject.getDataType(Client.class));
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		super.onRead(rawObject);
		Log.d(TAG, "onRead");
	}

}
