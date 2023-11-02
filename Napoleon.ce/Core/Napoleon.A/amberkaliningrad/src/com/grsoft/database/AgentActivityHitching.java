package com.grsoft.database;

import com.grsoft.dataobjects.AgentActivity;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.RawObject;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.exception.RuntimeException;

public class AgentActivityHitching implements ObjectExportListener {

	static final String FORCE_PUT_COMMAND = "FORCE PUT";
	
	AgentActivity agent;
	
	public AgentActivityHitching() {

		agent = new AgentActivity();
		agent.imei = ServerCommand.DeviceID;
		agent.phone = android.os.Build.MODEL;
		agent.login = ConfigManager.getConfig().login;
	}
	
	@Override public void onStart() { }
	@Override public void onRead(RawObject rawObject) throws RuntimeException {}
	@Override public void onSave() {}
	@Override public void onEnd() {}
	@Override public String getObjectName() { return "AgentActivity"; }

	@Override public int size() { return 1; }

	@Override public DataObject get(int i) { return i == 0 ? agent : null; }

}
