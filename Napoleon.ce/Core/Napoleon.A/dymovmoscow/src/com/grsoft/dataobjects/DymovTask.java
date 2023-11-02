package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="task_dymov", keyFields="idTask")
public class DymovTask extends DocDataObject implements Comparable<DymovTask> {
	public String idTask = "";
	public Date start;
	public String task;
	public int isPeriod;
	public String manager="";
	
	boolean isOwnTask = false; 
	
	public void loadFrom(DymovTaskResult data) {
		id = data.id;
		idTask = data.idTask;
		task = data.task;
		isPeriod = 0;
		start = data.created;
		date = new Date();
		
		isOwnTask = true;
	}
	
	public boolean isOwn() { return isOwnTask; }

	@Override
	public int compareTo(DymovTask another) {
		return date.compareTo(another.date);
	}
}
