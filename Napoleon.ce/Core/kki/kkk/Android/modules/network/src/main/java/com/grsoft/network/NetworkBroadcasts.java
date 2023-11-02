package com.grsoft.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class NetworkBroadcasts {
	public static final String SYNC_FINISHED = "com.grsoft.network.SYNC_FINISHED";
	public static final String SYNC_RESULT = "com.grsoft.network.SYNC_RESULT";

	public static void sendSyncResult(Context context, boolean result) {
		Intent i = new Intent(SYNC_FINISHED);
		i.putExtra(SYNC_RESULT, result);
		context.sendBroadcast(i);
	}
	
	public static void registerSyncResultReceiver(Context context, BroadcastReceiver receiver) {
		context.registerReceiver(receiver, new IntentFilter(SYNC_FINISHED));
	}
	
	public static boolean getSyncResult(Intent intent) {
		return intent.getBooleanExtra(SYNC_RESULT, false);
	}
}
