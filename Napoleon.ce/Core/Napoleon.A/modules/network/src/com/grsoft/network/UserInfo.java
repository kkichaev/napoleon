package com.grsoft.network;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;

public class UserInfo
{
	public static class ConnArg{
		public String address = "";
		public int port = 0;
	}

	private String user;
	private String password;
	private List<ConnArg> connArgs;
	public String impersonate;

	public UserInfo(String user, String password, String impersonate)
	{
		this.user = user;
		this.password = password;
		this.impersonate = impersonate;
		
		createDefaultConnparam();
	}

	public String getUser()
	{
		return user;
	}

	private void createDefaultConnparam() {
		connArgs = new ArrayList<UserInfo.ConnArg>();
		Config cfg = ConfigManager.getConfig();
		
		if(cfg != null){
			ConnArg cp = new ConnArg();
			cp.address = cfg.address;
			cp.port = cfg.port;
			
			connArgs.add(cp);
			
			cp = new ConnArg();
			cp.address = cfg.address2;
			cp.port = cfg.port2;
			
			connArgs.add(cp);
		}
	}

	public void setConnArg(ConnArg... args){
		connArgs = new ArrayList<UserInfo.ConnArg>();
		
		for(int i = 0; i < args.length; i++)
			connArgs.add(args[i]);
	}

	public void addConnArg(ConnArg arg){
		if (connArgs == null)
			connArgs = new ArrayList<UserInfo.ConnArg>();
		
		connArgs.add(arg);
	}
	
	public int getConnArgCount(){
		int result = 0;
		
		if(connArgs != null)
			result = connArgs.size();
		
		return result;
	}

	public ConnArg getConnArgAt(int index){
		ConnArg result = null;
		
		if(connArgs != null){
			int sz = connArgs.size();
			
			if(index >= 0 && index < sz)
				result = connArgs.get(index);
		}
		
		return result;
	}

	public String getPassword()
	{
		return password;
	}
	
	public boolean isValid(){
		return ((user != null && password != null && user.trim().length() > 0 && password.trim().length() > 0) ||
				(ServerCommand.DeviceID != null &&
					ServerCommand.DeviceID.length() > 0));
	}
}
