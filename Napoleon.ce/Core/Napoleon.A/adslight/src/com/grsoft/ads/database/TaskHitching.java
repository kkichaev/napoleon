package com.grsoft.ads.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.grsoft.ads.AdsConsts;
import com.grsoft.ads.TaskNotify;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.HitchOnSelect;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.dataobjects.TaskQuery;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.preference.PreferenceManager;
import android.util.Log;

public class TaskHitching extends HitchOnSelect {
	private SimpleDateFormat sdf =  new SimpleDateFormat("dd.MM.yyyy");
	private SQLiteStatement stm;
	private Context context;
	private static final String TASK_ALARM_CNT = "TASK_ALARM_CNT";
	
	public TaskHitching(Context context) {
		super(DbObject.getDataType(TaskQuery.class), "TaskQueryUser");
		setCondition(sdf.format(new Date()));
		this.context = context;
		
		try{
			SQLiteDatabase db = DataBaseManager.getDataBase();
			String tn = DataObjectInfo.getInstance().getTableName(dataObject);
			final String sql = "update " + tn + " set uptoday = 0";
			db.execSQL(sql);
			
			stm = db.compileStatement("select count(rowid) from " + tn + " where taskid = ?");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		TaskQuery task = (TaskQuery) rawObject.createDataObject(dataObject);
		
		Log.d("ADS", String.format("TaskHitching.read status: %d", task.solution));
		
		task.uptoday = 1;
		
		if(stm != null){
			stm.bindString(1, task.taskid);
			long cnt = stm.simpleQueryForLong();
			
			if(cnt == 0) {
				task.uptoday = 1;
				notify(task);
			}
			
			dbProxy.insertRecord(task);
		}
	}
	
	public static String TEST = "";
	
	private void notify(TaskQuery task) {
		Date date = getNotifyTime(task);

		if(date.getTime() > new Date().getTime()) 
			registerAlarm(task, date);
	}

	protected Date getNotifyTime(TaskQuery task) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(task.start);
		calendar.add(Calendar.MINUTE, -task.notify);
		return calendar.getTime();
	}

	protected void registerAlarm(TaskQuery task, Date date) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		int almid = pref.getInt(TASK_ALARM_CNT, 0);
		
		AlarmManager amr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, TaskNotify.class);
		i.putExtra(AdsConsts.TASKID, task.taskid);
		PendingIntent pi = PendingIntent.getBroadcast(context, almid, i, PendingIntent.FLAG_CANCEL_CURRENT);
		amr.set(AlarmManager.RTC_WAKEUP, date.getTime(), pi);
		
		Log.d(this.getClass().getCanonicalName(), 
				String.format("alarm registered on: %s, id = %d", date.toString(), almid));
		
		Editor ed = pref.edit();
		ed.putInt(TASK_ALARM_CNT, ++almid);
		ed.commit();
	}

	@Override
	public void onEnd() {
		if(stm != null)
			stm.close();
		
		super.onEnd();
	}
}
