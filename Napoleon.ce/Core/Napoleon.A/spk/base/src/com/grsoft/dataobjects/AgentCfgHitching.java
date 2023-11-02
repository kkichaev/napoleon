package com.grsoft.dataobjects;

import java.util.ArrayList;

import com.grsoft.database.Hitching;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ObjectExportListener;

public class AgentCfgHitching extends Hitching implements ObjectExportListener{
	public static final String AUTOMATIC_REQUEST_REPORT = "automatic_report_request";
	
    private class AgentCgf extends DataObject{
    	@SuppressWarnings("unused")
		public String name;
    	@SuppressWarnings("unused")
		public String value;
    }
    
    ArrayList<AgentCgf> list = new ArrayList<AgentCgf>();
    
	public AgentCfgHitching() {
		super(AgentCgf.class, "AgentCgf");
		
		AgentCgf arr = new AgentCgf();
		arr.name = AUTOMATIC_REQUEST_REPORT;
		arr.value = (Boolean)ConfigManager.getConfig()
				.getProperty(AgentCfgHitching.AUTOMATIC_REQUEST_REPORT) ? "1" : "0";
		
		list.add(arr);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public DataObject get(int i) {
		return list.get(i);
	}
}
