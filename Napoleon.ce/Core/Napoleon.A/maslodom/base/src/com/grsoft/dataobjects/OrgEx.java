package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	public int firm;
	
	@Scale(value=Consts.SUM_SCALE)
	public int limit;
	
	public List<OrgDiscount> discounts = new ArrayList<OrgDiscount>();
	
	//Внутреннее поле на сервер непередается
	//public int blocked = 0;
	
	@Override
	public boolean isStopList() {
		if (isPotencial())
			return false;
		else {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -40);
			StringBuilder where = new StringBuilder();
			where.append("created>").append(cal.getTime().getTime())
					.append(" and id='").append(id).append("'");
			List<Long> ids = DbReader.readIds(DataObjectInfo.getInstance()
					.getTableName(Order.class), where.toString(), null);
			return ids.size() == 0;
		}
	}
	
	public boolean isBlockedStop(){
		return super.isStopList();
	}
}
