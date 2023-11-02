package com.grsoft.dataobjects.impl;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.CreatableDocument;

public class OrderImplEx extends OrderImpl {
	
	@Override
	public String getDescription(Context context) {
		return (data.podRemark.length() > 0) ? data.podRemark : 
			(isProceeded()) ?  context.getString(R.string.in_processeng) : 
			(isExported()) ? context.getString(R.string.sent) : 
			""; 
	}
	
	@Override
	protected void postCopyProcess(CreatableDocument<Order> copy) {
		super.postCopyProcess(copy);
		
		if(copy instanceof OrderImpl) {
			OrderEx dest = (OrderEx) ((OrderImpl)copy).getData();
			OrderEx src = (OrderEx)data;
			
			Calendar c = Calendar.getInstance();
			Calendar c1 = Calendar.getInstance();
			
			c.setTime(new Date());
			c.add(Calendar.DAY_OF_MONTH, 1);
			if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
				c.add(Calendar.DAY_OF_MONTH, 1);
			}else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
				c.add(Calendar.DAY_OF_MONTH, 2);
			}
			
			c1.setTime(src.date);
			c.set(Calendar.HOUR_OF_DAY, c1.get(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, c1.get(Calendar.MINUTE));
			dest.date = c.getTime();
			
			dest.buh = src.buh;
			
			c1.setTime(src.date2);
			c.set(Calendar.HOUR_OF_DAY, c1.get(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, c1.get(Calendar.MINUTE));
			dest.date2 = c.getTime();
		}
	}
}
