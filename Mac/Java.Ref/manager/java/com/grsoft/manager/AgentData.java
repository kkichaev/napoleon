package com.grsoft.manager;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class AgentData {
	public int distance;
	
	public int visits;
	public int orders;	
	
	@Scale(value=Consts.SUM_SCALE)
	public long sum;
	public int progress;
}
