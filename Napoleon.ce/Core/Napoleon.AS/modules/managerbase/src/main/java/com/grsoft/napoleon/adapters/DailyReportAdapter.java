package com.grsoft.napoleon.adapters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.AgentReportData;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.manager.AgentData;
import com.grsoft.manager.ReportData;
import com.grsoft.manager.view.RowItem;
import com.grsoft.util.Util;

public class DailyReportAdapter extends ReportAdapter {

	public DailyReportAdapter(Context context, List<RowItem> agents, LayoutInflater inflater) {
		super(context, agents, inflater);
	}

	@Override
	protected ReportData loadData(Date curDate, boolean nextDate) {
		
		if( curDate == null )
			curDate = Util.getDate();
		else {
			Calendar c = Calendar.getInstance();
			c.setTime(curDate);
			c.add(Calendar.DAY_OF_MONTH, nextDate ? 1 : -1);
			curDate = c.getTime();
		}
				
		return new DailyData(agents,curDate);
	}
}

class DailyData extends ReportData {
	
	public DailyData(List<RowItem> agents, Date d) {
		super(d);
		
		DbWriter.checkDBTable(AgentReportData.class);
		AgentReportData ard = new AgentReportData();
		String table = DataObjectInfo.getInstance().getTableName(ard.getClass());
		String where = "start_date=" + Long.toString(d.getTime());
		DbReader r = new DbReader();
		boolean bdo = r.select(ard, table, where);
		while(bdo) {
			AgentData ad = new AgentData();
			ad.orders = ard.orders;
			ad.visits = ard.visits;
			ad.sum = ard.sum;
			ad.progress = ard.progress;
			ad.distance = ard.dist;
			
//			ad.parent = -1;
//			ad.level = 0;
//			ad.view = null;
//			ad.isCollapsable = false;
//			ad.collapsed = false;
			
			data.put(ard.id, ad);
			
			bdo = r.selectNext(ard);
		}
		r.close();
		
		calcDivisionData(agents);
	}

	static SimpleDateFormat format = new SimpleDateFormat("dd MMMM", Locale.getDefault());
	
	@Override
	public CharSequence getTitle() { return format.format(date); }

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