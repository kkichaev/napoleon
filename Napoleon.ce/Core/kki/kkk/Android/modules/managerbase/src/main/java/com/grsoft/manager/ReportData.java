package com.grsoft.manager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.grsoft.dataobjects.DivisionAgent;
import com.grsoft.manager.view.GroupRowItem;
import com.grsoft.manager.view.RowItem;


public abstract class ReportData {
	public abstract CharSequence getTitle();
	public abstract boolean isLast();
	public abstract Date getEndDate();
	
	public Date getDate() { return date; }
	
	/**
	 * или дата начала интервала, или просто дата для данных
	 */
	protected Date date;
	protected HashMap<String, AgentData> data = new HashMap<String, AgentData>();
	protected HashMap<String, AgentData> divisions_data = new HashMap<String, AgentData>();
	
	protected ReportData(Date date) {
		this.date = date;
	}
	
	public AgentData getAgentData(String id) {
		return data.get(id);
	}
	
	public AgentData getDivisionData(String id) {
		return divisions_data.get(id);
	}
	
	public void calcDivisionData(List<RowItem> infos) {
		divisions_data.clear();
		
		for(int i = 0; i < infos.size(); i++) {
			if(!(infos.get(i) instanceof GroupRowItem))
				continue;
			AgentData ad = new AgentData();
			int agentCount = 0;
			List<DivisionAgent> agents = ((GroupRowItem)infos.get(i)).allDivisionAgents;
			for(int j = 0; j < agents.size(); j++) {
				AgentData current_data = getAgentData(agents.get(j).id);
				if(current_data != null) {
					ad.visits += current_data.visits;
					ad.orders += current_data.orders;
					ad.sum += current_data.sum;
					ad.progress += current_data.progress;
					agentCount++;
				}
			}
			
			if(agentCount != 0)
				ad.progress = (ad.progress * 10 / agentCount + 5) /10; // округляем
			
			divisions_data.put(Integer.toString(((GroupRowItem)infos.get(i)).division.id), ad);
		}
		
	}
}
