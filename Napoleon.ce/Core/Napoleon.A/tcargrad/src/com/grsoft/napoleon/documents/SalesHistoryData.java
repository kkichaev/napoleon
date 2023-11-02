package com.grsoft.napoleon.documents;

import java.util.Date;

public class SalesHistoryData implements Comparable<SalesHistoryData> {
	public Date date;
	public int qty;
	public String taxName;
	
	public SalesHistoryData(Date d, int q, String t) {
		date = d;
		qty = q;
		taxName = t;
	}

	@Override
	public int compareTo(SalesHistoryData arg0) {
		int cmp = date.compareTo(arg0.date);
		if( cmp != 0 )
			return cmp;
		return taxName.compareTo(arg0.taxName);
	}
}
