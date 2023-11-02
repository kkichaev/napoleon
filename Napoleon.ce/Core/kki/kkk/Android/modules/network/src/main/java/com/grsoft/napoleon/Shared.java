package com.grsoft.napoleon;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;

public class Shared extends Service {
	private static final String LOGIN_PREF = "LoginData";
	private static final String DURATION_KEY = "Duration";
	
	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	private final IShared.Stub binder = new IShared.Stub() {

		@Override
		public void putDuration(int val) throws RemoteException {
			SharedPreferences prf = getSharedPreferences(LOGIN_PREF, Context.MODE_PRIVATE);
			SharedPreferences.Editor e = prf.edit();
			e.putInt(DURATION_KEY, val);
			e.commit();
			
		}

		@Override
		public int getDuration() throws RemoteException {
			SharedPreferences prf = getSharedPreferences(LOGIN_PREF, Context.MODE_PRIVATE);
			return prf.getInt(DURATION_KEY, 0);
		}
	};
}
