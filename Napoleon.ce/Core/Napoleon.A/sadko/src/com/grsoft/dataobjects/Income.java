package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;


@TableInfo(name="Incomes", keyFields="number")
public class Income extends DataObject {

	public Date date;
	public String number;
	
	public List<IncomeItem> items = new ArrayList<IncomeItem>();
}
