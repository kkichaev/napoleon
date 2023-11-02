package com.grsoft.dataobjects;

import java.util.Date;

public class ActionData {
	public Date start;
	public Date end;
	public Date startAction;
	public Date endAction;
	public int cost;
	
	public ActionData(TradeAction action, TradeActionItem item) {
		start = action.start;
		end = action.end;
		startAction = action.startAction;
		endAction = action.endAction;
		
		cost = item.cost;
	}
}