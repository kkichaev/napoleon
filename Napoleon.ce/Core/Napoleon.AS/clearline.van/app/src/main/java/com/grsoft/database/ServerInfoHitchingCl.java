package com.grsoft.database;

import com.grsoft.dataobjects.ServerInfoObject;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import android.database.SQLException;
import android.os.SystemClock;

public class ServerInfoHitchingCl extends Hitching {
	
	public ServerInfoHitchingCl() {
		super(ServerInfoObject.class, "%ServerInfo");
	}

	@Override
	public void onStart() {
		DbWriter.checkDBTable(dataObject);
		dbProxy.startProcess(COMMIT_INTERVAL);
	}

	@Override
	public void prepareReading() {
		super.prepareReading();
	}

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {

		ServerInfoObject obj = (ServerInfoObject) rawObject.createDataObject(dataObject);
		obj.elapsedTime = SystemClock.elapsedRealtime();
		try {
			String stmt = "DELETE FROM " + obj.getTableName();
			DataBaseManager.getDataBase().execSQL(stmt);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dbProxy.insertRecord(obj);
	}
}
