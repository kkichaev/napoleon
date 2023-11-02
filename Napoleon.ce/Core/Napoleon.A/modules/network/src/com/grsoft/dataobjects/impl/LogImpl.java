package com.grsoft.dataobjects.impl;

import java.util.Calendar;
import com.grsoft.dataobjects.Log;
import com.grsoft.napoleon.util.ConfigManager;

public class LogImpl extends DbObject<Log>{
	public static void log(int type, int category,  String comments){
		if (ConfigManager.getConfig().loggable)
			logwr(type, category, comments);
	}

	private static void logwr(int type, int category, String comments) {
		LogImpl logImpl = new LogImpl();
		Log log = logImpl.getData();
		log.date = Calendar.getInstance().getTime();
		log.unixtime = log.date.getTime();
		log.action = type;
		log.comments = comments;
		log.category = category;
		logImpl.write();
	}
	
	public static void log(int type){
		log(type, Log.MANAGER, "");
	}
	
	public static void logd(int type, String commnets){
		log(type, Log.DEBUG, commnets);
	}
	
	public long write() {
		rowid = getWriter().insertRecord(data);
		close();
		
		return rowid;
	}
}
