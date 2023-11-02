package com.grsoft.network;

public class UserInfo
{
	private String user;
	private String password;

	public UserInfo(String user, String password)
	{
		this.user = user;
		this.password = password;
	}

	public String getUser()
	{
		return user;
	}

	public String getPassword()
	{
		return password;
	}
	
	public boolean isValid(){
		return ((user != null && password != null && user.trim().length() > 0 && password.trim().length() > 0) || 
				ServerCommand.DeviceID.length() > 0);
	}
}
