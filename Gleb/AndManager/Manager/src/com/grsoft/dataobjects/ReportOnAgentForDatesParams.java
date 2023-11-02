package com.grsoft.dataobjects;

import java.util.Date;

public class ReportOnAgentForDatesParams extends DataObject {
	
	public Date start_date;
	public Date end_date;
	
	public ReportOnAgentForDatesParams() {}
	
	public ReportOnAgentForDatesParams(Date start, Date end) {
		start_date = start;
		end_date = end;
		long diff = end_date.getTime() - start_date.getTime();
		if( diff < 3600l * 24 * 7 * 1000l) {
			start_date = new Date(end_date.getTime() - 3600l * 24 * 7 * 1000l);
		}
	}
}
