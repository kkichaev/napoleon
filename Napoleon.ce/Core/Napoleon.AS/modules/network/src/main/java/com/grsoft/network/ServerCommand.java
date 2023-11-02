/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 *
 *
 * kki   12/10/2010   creating
 */
package com.grsoft.network;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.CommandArgs;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.types.Scale;

@TableInfo(name = "ServerCommand")
public class ServerCommand extends DataObject
{
	public static String ProgramVersion = "";
	public static String Category="pda";
	public static String DeviceID = "";
	public static String Project = "";

	public ServerCommand(LoginData loginData) 
	{
		this.command = new String();
		this.param = new String();
		this.userid = loginData.getUser();
		this.password = loginData.getPassword();

		this.version = ProgramVersion;
		this.duration = loginData.getDuration();
		this.category = Category;
		this.progid = DeviceID;
		this.project = Project;

		this.uuid = loginData.getUuid();
	}
	
	public void setCommandParams(CommandArgs args) throws RuntimeException
	{
		command = args.getCommand();
		param = args.getParams();
	}
	
	public void setCommandParams(String cmd, String param) {
		this.command = cmd;
		this.param = param;
	}

	public String command;
	public String param;
	public String userid;
	public String password;
	public String version;
	public String category;
	public String progid;
	public String project;

	public String uuid;
	
	@Scale(value=1)
	public int duration;
}
