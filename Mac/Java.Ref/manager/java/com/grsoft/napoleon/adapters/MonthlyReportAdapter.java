package com.grsoft.napoleon.adapters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentReportData;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.manager.AgentData;
import com.grsoft.manager.ReportData;
import com.grsoft.manager.view.RowItem;
import com.grsoft.util.Util;

public class MonthlyReportAdapter extends ReportAdapter {

	public MonthlyReportAdapter(Context context, List<RowItem> agents, LayoutInflater inflater) {
		super(context, agents, inflater);		
	}

	@Override
	protected ReportData loadData(Date curDate, boolean nextDate) {
		if( curDate == null ) {
			Calendar calendar = Calendar.getInstance();			
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);		
				
			calendar.set(year, month, 1);
			
			curDate = calendar.getTime(); 
		}
		else {
			Calendar c = Calendar.getInstance();
			c.setTime(curDate);
			
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);			
			
			month += nextDate ? 1 : -1;
			
			if (month < 0) {
				month += 12;
				year--;
			} 
			else if(month>=12) {
				month -=12;
				year++;
			}				
				
			c.set(year, month, 1, 0, 0, 0);		
			
			curDate = c.getTime();
		}
				
		return new MonthlyData(agents,curDate);
	}

}


class MonthlyData extends ReportData {	

	static SimpleDateFormat format = new SimpleDateFormat("MMMM''yy", Locale.getDefault());

	Date lastDate;
	
	public MonthlyData(List<RowItem> agents, Date d) {
		super(d);		

		Calendar c = Calendar.getInstance();
		c.setTime(date);
    	int max_day = c.getActualMaximum(Calendar.DAY_OF_MONTH);    	
    	c.set(Calendar.DAY_OF_MONTH, max_day);		
		lastDate = c.getTime();
		
//		String log_str = Integer.toString(hashCode()) + 
//				" - " +	new SimpleDateFormat("yyyy.MM.dd").format(date) + 
//				" - " + new SimpleDateFormat("yyyy.MM.dd").format(lastDate);
//		
//		Log.d("007","MonthlyData - "+log_str);
		
		AgentReportData ard = new AgentReportData();
		String table = DataObjectInfo.getInstance().getTableName(ard.getClass());
		String where = "start_date>=" + Long.toString(date.getTime())+
				"AND start_date<=" + Long.toString(lastDate.getTime());;
		DbReader r = new DbReader();
		boolean bdo = r.select(ard, table, where);
		while(bdo) {
			if(data.containsKey(ard.id)){			
			
				AgentData ad = data.get(ard.id);
				
				ad.orders += ard.orders;
				ad.visits += ard.visits;
				ad.sum += ard.sum;
				ad.progress += ard.progress;				
			}
			else {
				AgentData ad = new AgentData();
				
				ad.orders = ard.orders;
				ad.visits = ard.visits;
				ad.sum = ard.sum;
				ad.progress = ard.progress;
				
				data.put(ard.id, ad);
			}
			
			bdo = r.selectNext(ard);
		}
		r.close();
		
		calcDivisionData(agents);
	}

	@Override
	public CharSequence getTitle() {		
		return format.format(date);
	}

	@Override
	public boolean isLast() {
		Date now = Util.getDate(); 
		return now.equals(date) || date.after(now);
	}

	@Override
	public Date getEndDate() {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}	
}