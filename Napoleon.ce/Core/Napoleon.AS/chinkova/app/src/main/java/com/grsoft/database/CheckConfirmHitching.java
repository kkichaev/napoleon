package com.grsoft.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.grsoft.dataobjects.CheckConfirm;
import com.grsoft.dataobjects.ChekBase;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.RequestChek;
import com.grsoft.dataobjects.ReturnChekBack;
import com.grsoft.dataobjects.impl.RequestChekImpl;
import com.grsoft.dataobjects.impl.ReturnChekBackImpl;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class CheckConfirmHitching extends HitchOnSelect {
	static String errorText = null;
	
//	Date begin = new Date();
	int monthDelta;
	int dayDelta;

	SQLiteStatement stmtChek;
	SQLiteStatement stmtChekBack;
	
	RequestChekImpl chek;
	ReturnChekBackImpl chkBack;
	
	public CheckConfirmHitching(int monthDelta, int dayDelta) {
		super(CheckConfirm.class, "CheckConfirm");
//		begin = start;
		this.monthDelta = monthDelta;
		this.dayDelta = dayDelta;
		
		chek = new RequestChekImpl();
		chkBack = new ReturnChekBackImpl();
	}
	
	SQLiteStatement makeStmt(ChekBase cb) {
		DbWriter.checkDBTable(cb.getClass());
		
		SQLiteDatabase db = DataBaseManager.getDataBase();
		String stmt = "update " + cb.getTableName() + " SET params = (params | ?), qrcode=?, handleRemark=?, handleStatus=?, handleChanged=? where created = ?";
		return db.compileStatement(stmt);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		stmtChek = makeStmt(chek.getData());
		stmtChekBack = makeStmt(new ReturnChekBack());
		
		errorText = null;
	}
	
	public static String getErrorText() {
		String ret = errorText;
		errorText = null;
		return ret;
	}
	
	@Override
	public void onEnd() {
		super.onEnd();

		try {
			if( stmtChek != null )
				stmtChek.close();
			if(stmtChekBack != null)
				stmtChekBack.close();
			chek.close();
			chkBack.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected String getCondition() {
		Calendar c = Calendar.getInstance(Locale.getDefault());
		c.setTime(new Date());
		c.add(Calendar.MONTH, -monthDelta);
		c.add(Calendar.DATE, -dayDelta);
		
		SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
		String where = String.format("\"userid\" = '$CURRENT_USERID' and \"handled\" >= ToDate('%s 00:00:00')", simpleDateFormat.format(c.getTime()));
		return where;
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		CheckConfirm cc = (CheckConfirm) rawObject.createDataObject(dataObject); 
		SQLiteStatement stmt = null;
		Date handled = ServerInfoHitchingChinkova.getLocalDate(cc.handled);
		if(cc.type == -1) {
			errorText = cc.remark;
			return;
		} else if(cc.type == 0) {
			stmt = stmtChekBack;
			
			ReturnChekBack rcb = chkBack.getData();
			rcb.created = cc.created;
			if(chkBack.read() && rcb.handleChanged.compareTo(rcb.created) != 0 && rcb.handleChanged.compareTo(handled) > 0)
				return;
		} else {
			stmt = stmtChek;

			RequestChek rd = chek.getData();
			rd.created = cc.created;
			if( chek.read() && rd.handleChanged.compareTo(rd.created) != 0 && rd.handleChanged.compareTo(handled) > 0)
				return;
		}
		stmt.clearBindings();
		stmt.bindLong(1, ParamState.ofProceeded | ParamState.ofExported);
		stmt.bindString(2, cc.qrcode);
		stmt.bindString(3, cc.remark);
		stmt.bindLong(4, cc.status);		
		stmt.bindLong(5, handled.getTime());
		stmt.bindLong(6, cc.created.getTime());
		stmt.execute();
	}
}
