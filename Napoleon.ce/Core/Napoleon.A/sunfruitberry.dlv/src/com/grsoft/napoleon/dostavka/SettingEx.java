package com.grsoft.napoleon.dostavka;

import com.grsoft.network.SyncProgress;
import android.annotation.SuppressLint;
import android.content.Intent;

public class SettingEx extends Setting {
	SyncProgress progress = new SyncProgress();
	
	@SuppressLint("SimpleDateFormat")
	public void doRestore(int months) {
		
		Intent i = new Intent(MainEx.RESTORE_BASE);
		i.putExtra(MainEx.RESTORE_MONTHS, months);
		sendBroadcast(i);
		//finish();
	}
}
