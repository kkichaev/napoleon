package com.grsoft.database;

import android.database.sqlite.SQLiteStatement;
import com.grsoft.dataobjects.OrderProceeded;
import com.grsoft.dataobjects.OrderProceededEx;


public class PODHitchingEx extends PODHitching {
	
	@Override
	protected ProceededDocHandler createHandler() {
		return new Handler();
	}
}

class Handler extends ProceededDocHandler {
	@Override
	protected String getParamStmt() {
		return "' SET params = (params | ?), podRemark = ?, fio = ?, phone = ? WHERE created = ?";
	}
	
	@Override
	protected void bindArgs(OrderProceeded proceeded, int param, SQLiteStatement stmt) {
		stmt.bindLong(1, param);
		stmt.bindString(2, proceeded.remark);
		OrderProceededEx pe = (OrderProceededEx) proceeded;
		stmt.bindString(3, pe.fio);
		stmt.bindString(4, pe.phone);
		stmt.bindLong(5, proceeded.created.getTime());
	}
}