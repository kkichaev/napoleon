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

public class WeeklyReportAdapter extends ReportAdapter {

	public WeeklyReportAdapter(Context context, List<RowItem> agents, LayoutInflater inflater) {
		super(context, agents, inflater);
	}

	@Override
	protected ReportData loadData(Date curDate, boolean nextDate) {
		if( curDate == null ) {			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(Util.getDate());			
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			curDate = calendar.getTime();
		}
		else {
			Calendar c = Calendar.getInstance();
			c.setTime(curDate);
			c.add(Calendar.DAY_OF_MONTH, nextDate ? 7 : -7);
			curDate = c.getTime();
		}
				
		return new WeeklyData(agents,curDate);
	}

}


class WeeklyData extends ReportData {
	
	Date lastDate;
	
	public WeeklyData(List<RowItem> agents, Date d) {
		super(d);

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, 6);
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

	static SimpleDateFormat format = new SimpleDateFormat("dd MMMM", Locale.getDefault());
	
	@Override
	public CharSequence getTitle() { 
		return format.format(date) + " - " + format.format(lastDate);
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