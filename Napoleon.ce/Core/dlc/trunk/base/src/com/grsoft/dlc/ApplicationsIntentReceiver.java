package com.grsoft.dlc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ApplicationsIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
    	SharedPreferences pref = context.getSharedPreferences(Preferences.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    	Editor ed = pref.edit();
    	ed.putBoolean(DLCApp.PKG_SET_CHANGED, true);
    	ed.commit();
    }
}