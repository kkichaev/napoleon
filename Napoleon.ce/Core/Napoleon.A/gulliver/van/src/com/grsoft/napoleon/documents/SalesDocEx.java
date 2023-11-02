package com.grsoft.napoleon.documents;

import java.util.Date;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.util.DatePeriod;

public class SalesDocEx extends SalesDoc {
	public SalesDocEx() {
		super(SalesImplEx.class);
	}
	
	public static void init(){
		instance = new SalesDocEx();
	}
	
	@Override
	public boolean removeTill(Date tillDate) {
		DatePeriod dp = new DatePeriod(new Date(0), tillDate);
		DocList dl = docList(null, null, dp);
		for(Document<?> d : dl)
			d.delete();
		
		return true;
	}
}
