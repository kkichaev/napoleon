package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.util.Consts;

@TableInfo(name="returnCommit", keyFields="created")
@ServerInfo(name="ReturnCommit")
public class ReturnCommit extends DataObject {
	public Date created = new Date();
	
	public String id = "";
	
	public List<ReturnCommitItem> items = new ArrayList<ReturnCommitItem>();
	
	public static ReturnCommit get(Date created) {
		ReturnCommit ret = new ReturnCommit();
		
		DbReader r = new DbReader();
		String where = "created = " + Long.toString(created.getTime());
		if( !r.select(ret, ret.getTableName(), where) )
			ret = null;
		r.close();
		
		return ret;
	}
	
	public void processReturn() {
		ReturnEx re = new ReturnEx();
		String stmt = "update " + re.getTableName() + " set params=(params | " + Integer.toString(ParamState.ofProceeded) + 
				"), commitSum = " + Long.toString(sum()) + " where created=" + Long.toString(created.getTime());
		
		try {
			DataBaseManager.getDataBase().execSQL(stmt);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public long sum() {
		long sum = 0;
		
		for(ReturnCommitItem i : items) {
			sum += (long)i.cost * i.qty / Consts.QTY_SCALE;
		}
		
		return sum;
	}
}
