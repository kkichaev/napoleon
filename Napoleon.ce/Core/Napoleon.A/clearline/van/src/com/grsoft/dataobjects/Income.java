package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="Income", keyFields="date")
@ServerInfo(name="Incomes")
public class Income extends DataObject {
	public Date date;
	
	public List<IncomeItem> items = new ArrayList<IncomeItem>();
}
