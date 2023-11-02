package com.grsoft.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.database.TextLog;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.MGpsPos;
import com.grsoft.dataobjects.Mapgis;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.PotenzialOrg;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Question;
import com.grsoft.dataobjects.RouteParam;
import com.grsoft.manager.documents.MDocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.util.DatePeriod;

public class SyncDetail {

	public static String SYNC_DETAIL_MODULE_NAME = "route";

	public static void sync(Context context, UpdateCtrl pown, String userid, Date date, boolean useCash) {
		if (useCash && haveUserData(userid, date))
			pown.onFinish(true);
		else 
			startSync(context, pown, userid, date);
	}

	private static void startSync(Context context, UpdateCtrl pown, String userid, Date date) {
		List<Hitching> ret = new ArrayList<Hitching>();
		List<Hitching> repResult = new ArrayList<Hitching>();

		addData(repResult);
		addDocs(repResult);

		ret.add(new ReportHitching(SYNC_DETAIL_MODULE_NAME, createParam(userid, date), repResult));

		UpdateProcess upp = new UpdateProcess((Activity) context, pown, ret);
		upp.execute((Void[]) null);
	}

	private static boolean haveUserData(String userid, Date date) {
		boolean result = false;
		
		result = checkUserData(Mapgis.class, userid, date);
		
		if (!result)
			result = haveUserDocData(userid, date);
			
		return result;
	}

	private static boolean haveUserDocData(String userid, Date date) {
		boolean result = false;
		
		for (DocTypeBase dt : DocTypeBase.docTypes){
			result = checkUserData(((MDocType)dt).dataType(), userid, date);
			
			if (result)
				break;
		}
		
		return result;
	}

	private static boolean checkUserData(Class<? extends DataObject> type, String userid, Date date){
		boolean result = false;
		Cursor c = null;
		DbWriter.checkDBTable(type);
		
		try{
			StringBuilder where = new StringBuilder();
			where.append("SELECT COUNT(*) from [").append(DataObjectInfo.getInstance().getTableName(type))
				.append("] WHERE userid = ? AND created >= ? and created < ?");
			DatePeriod dp = DatePeriod.createRange(date, DatePeriod.MIN_PER_DAY);
			c = DataBaseManager.getDataBase().rawQuery(where.toString(), new String[]{ userid, Long.toString(dp.begin.getTime()),  Long.toString(dp.end.getTime())});
			
			if(c.moveToFirst())
				result = c.getInt(0) > 0;
				
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (c != null)
				c.close();
		}
		
		return result;
	}
	
	private static RouteParam createParam(String userid, Date date) {
		RouteParam result = new RouteParam();
		result.userid = userid;
		result.date = date;

		return result;
	}

	private static void addData(List<Hitching> repResult) {
		repResult.add(new Hitching(Mapgis.class));
		repResult.add(new Hitching(Org.class));
		repResult.add(new Hitching(PotenzialOrg.class));
		repResult.add(new Hitching(TextLog.class));
		repResult.add(new Hitching(Price.class));
		repResult.add(new Hitching(Price.class, "ManagerPrice"));
		repResult.add(new Hitching(MGpsPos.class));
	}

	private static void addDocs(List<Hitching> repResult) {
		for (DocTypeBase dt : DocTypeBase.docTypes) 
			repResult.add(((MDocType)dt).getRcvHitch());

		repResult.add(new Hitching(Question.class, "Question"));
		repResult.add(new Hitching(ScriptDef.class, "ScriptDef"));
	}
}
