package com.grsoft.database;

import com.grsoft.dataobjects.OrderProceeded;
import com.grsoft.dataobjects.SPOD;
import com.grsoft.dataobjects.impl.DbObject;

import android.database.sqlite.SQLiteStatement;

public class SPODHitching extends PODHitching {
	
	public SPODHitching() {
		super(DbObject.getDataType(SPOD.class), "OrderProceeded");
	}
	
	@Override
	protected ProceededDocHandler createHandler() {
		return new ProceededDocHandler(){
			String type = "";
			
			@Override
			public void handle(OrderProceeded proceeded, int param) {
				type = proceeded.type;
				super.handle(proceeded, param);
			}
			
			@Override
			protected String getParamStmt() {
				if (numberType())
					return "' SET params = (params | ?), podRemark = ?, number = ? WHERE created = ?";
				else
					return super.getParamStmt();
			}

			protected boolean numberType() {
				return type.equals("Sales") || type.equals("Inventory") || type.equals("OrderCharge") || type.equals("Order");
			}
			
			@Override
			protected void bindArgs(OrderProceeded proceeded, int param, SQLiteStatement stmt) {
				if (numberType()) {
					stmt.bindLong(1, param);
					stmt.bindString(2, proceeded.remark);
					stmt.bindString(3, ((SPOD)proceeded).number);
					stmt.bindLong(4, proceeded.created.getTime());
				}else
					super.bindArgs(proceeded, param, stmt);
			}
		};
	}
}
