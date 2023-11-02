/*
 * Copyright (C), 2011, √ильди€ –азработчиков
 *
 * kki   05/07/2011   creating
 */
package com.grsoft.network;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;

/***
 * 
 * @author kki
 *
 */
public class ConnectionManager {
	private List<SocketConnection> connPool = new ArrayList<SocketConnection>();
	private SocketConnection conn;
	private static ConnectionManager instance;
	
	/***
	 * makePool требуетс€ дл€ создани€ пула сокетов,
	 * после первого инстацировани€ мы создаем пул, и он
	 * живет до того, как не были закрыты соединени€.
	 *
	 * ≈то сделано дл€ того чтобы можно было схватывать
	 * настроики в соединени€ после запуска программы
	 */
	boolean makePool = true;;
	
	private ConnectionManager(){}
	
	public SocketConnection get(int index){
		if(index < 0 || index >= connPool.size())
			return null;
		
		return connPool.get(index);
	}
	
	public static synchronized ConnectionManager getInstance(){
		if (instance == null)
			instance = new ConnectionManager();
		
		if (instance.makePool){
			instance.updatePool();
			instance.makePool = false;
		}
		
		return instance;
	}
	
	private synchronized void updatePool() {
		Config config = ConfigManager.getConfig();
		connPool.clear();
		
		connPool.add(new SocketConnection(
				config.address, config.port));
		connPool.add(new SocketConnection(
				config.address2, config.port2));
	}

	public SocketConnection getWorkConnection(){
		return conn;
	}
	
	public synchronized void setWorkConnection(SocketConnection conn){
		this.conn = conn;
	}
	
	public synchronized void endSession(){
		makePool = true;
	}
}
