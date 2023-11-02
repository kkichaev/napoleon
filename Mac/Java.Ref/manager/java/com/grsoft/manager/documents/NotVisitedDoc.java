package com.grsoft.manager.documents;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.NotVisitedImpl;
import com.grsoft.manager.R.string;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

public class NotVisitedDoc extends MDocType {
	static protected NotVisitedDoc instance = null;
	private static final String OBJ_NAME = "NotVisitedOrg";
	
	protected NotVisitedDoc() {
		this(NotVisitedImpl.class);
	}
	
	protected NotVisitedDoc(Class<? extends NotVisitedImpl> type){
		super(OBJ_NAME, type);
	}

	static public MDocType instance() {
		if( instance == null )
			instance = new NotVisitedDoc();
		return instance;
	}
	
	static public MDocType instance(Class<? extends NotVisitedImpl> type) {
		instance = new NotVisitedDoc(type);
		return instance;
	}

	@Override
	public List<Document<?>> userDoc(String userid, Date date) {
		List<Document<?>> result = new ArrayList<Document<?>>();
		
		date = Util.resetTime(date);
		DatePeriod dp = DatePeriod.createRange(date, DatePeriod.MIN_PER_DAY);
		
		Cursor c = null;
		
		try{
			final String ROWID = "rowid";
			String table = DataObjectInfo.getInstance().getTableName(getDataType());
			DbWriter.checkDBTable(getDataType());
			c = DataBaseManager.getDataBase().query(table, new String[]{ROWID}, "userid = ? and date >= ? and date < ?", 
					new String[]{userid, Long.toString(dp.begin.getTime()), Long.toString(dp.end.getTime())}, null, null, null);
			
			while(c.moveToNext()){
				long rowid = c.getLong(c.getColumnIndex(ROWID));
				Document<?> d = create();
				
				if(d.read(rowid))
					result.add(d);
				
				d.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (c != null)
				c.close();
		}
		
		return result;
	}
	
	@Override
	public int getDocTitle() { return string.not_visited_doc_title;	}
	
	@Override public Hitching getRcvHitch() { return new RcvNewHitching(getDataType(), objName); }
}
