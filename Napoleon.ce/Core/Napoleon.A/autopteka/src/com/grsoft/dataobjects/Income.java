package com.grsoft.dataobjects;

import java.util.List;
import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="Incomes", keyFields="date")
public class Income extends DataObject {
	
	public Date date;
	
	public List<IncomeItem> items;
}
