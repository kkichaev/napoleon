package com.grsoft.dataobjects;

import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="arrival", keyFields="number")
public class Arrival extends DataObject {
	public String number;
	public Date date;
	
	public List<ArrivalItem> items;
	
	public int sum() {
		int sum = 0;
		
		if( items != null )
			for(ArrivalItem i : items)
				sum += i.sum;
		
		return sum;
	}
}
