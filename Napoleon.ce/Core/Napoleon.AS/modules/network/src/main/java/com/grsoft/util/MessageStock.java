/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Временно хранит rowID новых сообщений,
 * пока они не будут кем-то попрошены,
 * после этого они удаляются
 *
 * kki   08/04/2011   creating
 */
package com.grsoft.util;

import java.util.ArrayList;

import android.database.sqlite.SQLiteStatement;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Message;
import com.grsoft.dataobjects.impl.MessageNewImpl;
import com.grsoft.napoleon.UpdateMessageBox;

public class MessageStock {
	static boolean rcvUpdateMessage = false;

	private static MessageStock instance;
	private ArrayList<Message> message = new ArrayList<Message>();
	
	private static MessageStock getInstance(){
		if (instance == null) 
			instance = new MessageStock();
		
		return instance;
	}
	
	public static void add(Message message){
		if(message.kind.equals(UpdateMessageBox.UPDATE_KIND)) {
			if(rcvUpdateMessage) {
				return;
			}
			rcvUpdateMessage = true;
		}

		getInstance().message.add(message);
	}
	
	public static synchronized Message[] getNewMessage(){
		Message[] result = new Message[getInstance().message.size()];
		
		for(int i = 0; i < result.length; i++){
			result[i] = getInstance().message.get(i);
		}
		
		getInstance().message.clear();
		rcvUpdateMessage = false;

		try{
			DbWriter w = new DbWriter();
			for(Message m : result) {
				w.insertRecord(m);
			}
			w.close();

//			String tableName = DataObjectInfo.getInstance().getTableName(Message.class);
//
//			if(!DbWriter.isTableExists(tableName))
//				DbWriter.createTable(Message.class);
//
//			StringBuilder sql = new StringBuilder();
//			sql.append("INSERT INTO ").append(tableName).
//				append(" (date, message) VALUES (?, ?)");
//
//			SQLiteStatement stm = DataBaseManager.getDataBase().compileStatement(sql.toString());
//
//			for(Message m : result) {
//				stm.clearBindings();
//				stm.bindLong(1, m.date.getTime());
//				stm.bindString(2, m.message);
//				stm.executeInsert();
//			}
//
//			stm.close();
		}catch(Exception e){
			e.printStackTrace();
		}

		return result;
	}
}
