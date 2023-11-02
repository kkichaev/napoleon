package com.grsoft.network;
import com.grsoft.aceteam.R;

import com.grsoft.dataobjects.DataObjectPool;
import com.grsoft.network.exception.RuntimeException;

public class ByeCommand extends ServerCommand {

	public ByeCommand(LoginData loginData) {
		super(loginData);
		command = "BYE";
	}

	static public DataObjectPool dbPool(LoginData loginData) {
		DataObjectPool pool = new DataObjectPool();
		try {
			pool.add(new ByeCommand(loginData));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pool;
	}
}
