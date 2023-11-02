package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="ReportData", keyFields="id,start_date")
public class AgentReportData extends DataObject {
	public int division_id;
	public String id;
	public Date start_date;
	public Date end_date;
	public int visits;
	public int orders;
	
	@Scale(value=Consts.SUM_SCALE)
	public long sum;
	public int progress;
	public int order_progress;
	
	@Scale(value=Consts.DISTANCE_SCALE)
	public int dist;
	
	public int orgsz;
	public int plan;
	public int visited;
	public int plannedVisits;
}
