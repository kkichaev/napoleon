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
import com.grsoft.network.UserInfo.ConnArg;

/***
 * 
 * @author kki
 *
 */
public class ConnectionManager {
	public static Class<? extends ConnectionManager> connectionManager = ConnectionManager.class;
	protected List<SocketConnection> connPool = new ArrayList<SocketConnection>();
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
	
	protected ConnectionManager(){}
	
	public SocketConnection get(int index){
		if(index < 0 || index >= connPool.size())
			return null;
		
		return connPool.get(index);
	}
	
	public static synchronized ConnectionManager getInstance(){
		if (instance == null)
			instance = createInstance();
		
		if (instance.makePool){
			instance.updatePool();
			instance.makePool = false;
		}
		
		return instance;
	}
	
	private synchronized void updatePool() {
		Config config = ConfigManager.getConfig();
		connPool.clear();
		
		if(GRJSHelper.IsJSAddress(config.address)) {
			connPool.add(new SocketConnection(config.address, config.port));
		} else if(GRJSHelper.IsJSAddress(config.address2)) {
			connPool.add(new SocketConnection(config.address2, config.port));
		} else {
			if(config.address != null && config.address.length() > 0)
				connPool.add(new SocketConnection(config.address, config.port));
			if(config.address2 != null && config.address2.length() > 0)
				connPool.add(new SocketConnection(config.address2, config.port2));
		}
		postUpdatePool(config);
	}

	protected void postUpdatePool(Config cfg) {}
	
	private static ConnectionManager createInstance(){
		ConnectionManager result = new ConnectionManager();
		
		try{
			result = connectionManager.newInstance();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	protected synchronized void createPool(UserInfo info) {
		if(connPool != null && info != null){
			int sz = info.getConnArgCount();
			
			if(sz > 0){
				connPool.clear();
				
				for(int i = 0; i < sz; i++){
					ConnArg arg = info.getConnArgAt(i);
					if(GRJSHelper.IsJSAddress(arg.address)) {
						connPool.clear();
						connPool.add(new SocketConnection(arg.address, arg.port));
						break;
					}
					connPool.add(new SocketConnection(arg.address, arg.port));
				}
			}
		}
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
	
	public int getCount(){ return connPool.size(); }
}
