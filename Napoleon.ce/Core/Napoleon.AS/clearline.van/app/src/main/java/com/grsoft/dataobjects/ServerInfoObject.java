package com.grsoft.dataobjects;

import android.os.SystemClock;

import java.util.Date;

import com.grsoft.database.TableInfo;

public class ServerInfoObject extends ServerInfo {
	public long elapsedTime = SystemClock.elapsedRealtime();;
}
