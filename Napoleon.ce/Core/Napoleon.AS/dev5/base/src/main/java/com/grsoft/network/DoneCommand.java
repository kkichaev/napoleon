/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   16/07/2011   creating
 */
package com.grsoft.network;
import com.grsoft.aceteam.R;

import com.grsoft.dataobjects.DataObjectPool;
import com.grsoft.network.exception.RuntimeException;

/***
 * Запрос на передечу следующего пакета
 * 
 * Команда посылается серверу
 * если предыдущий пакет пришел с флагом CONTINUE
 * 
 * 
 * @author kki
 *
 */
public class DoneCommand extends ServerCommand {

	public DoneCommand(LoginData loginData) {
		super(loginData);
		command = "DONE";
	}

	static public DataObjectPool dbPool(LoginData loginData) {
		DataObjectPool pool = new DataObjectPool();
		try {
			pool.add(new DoneCommand(loginData));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return pool;
	}
}
