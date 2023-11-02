package com.grsoft.util;

import com.grsoft.napoleon.SendDocsService;

import android.content.Context;
import android.content.Intent;

public class BootUpRecevierEx extends BootUpReceiver {
	@Override
	public void onReceive(Context context, Intent arg1) {
		super.onReceive(context, arg1);
		SendDocsService.registerService(context);
	}
}
