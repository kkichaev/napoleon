package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import java.util.Date;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

/***
 * Данные о синхронизации с сервером 
 * @author kkichaev
 * 16.01.2015
 */

@TableInfo(name="syncinfo", keyFields="created")
@ServerInfo(name="SyncInfo")
public class SyncInfo extends DataObject {
	
	public static final int CLEAR 		= 1;
	public static final int GEN_DATA 	= 2;
	public static final int DOCS 		= 4;
	public static final int VISIT 		= 8;
	public static final int INCASS 		= 16;
	public static final int PRESENT     = 32;
	public static final int COST        = 64;
	public static final int DEBT 		= 128;
	public static final int RESTORE 	= 256;
	
	/***
	 * Дата
	 */
	public Date created;
	
	/***
	 * Набор выбранныйх значений параметров синхронизации
	 */
	public long syncparam;
	
	/*Резервирую, вдруг будем передавать на сервер*/
	public int params;
	
	/***
	 * Стату синхронизации 1 - успешно, 0 - ошибка
	 */
	public int result;
	
	/***
	 * Сообщение об ошибке
	 */
	public String errmsg;
	
	/***
	 * Логин
	 */
	public String login;
	
	/***
	 * Пароль 
	 */
	public String password;
	
	/***
	 * IP 1
	 */
	public String ip1;
	
	/***
	 * Порт 1
	 */
	public int port1;
	
	/***
	 * IP 2
	 */
	public String ip2;
	
	/***
	 * Порт 2
	 */
	public int port2;
	
	/***
	 * Период восстановления документов
	 */
	public int restore;
	
	/**
	 * MAC-address
	 */
	public String deviceID;
	
	
	public static Date getLastSync(int flag) {
		Date ret = null;
		
		DbReader r = new DbReader();
		SyncInfo data = new SyncInfo();
		if(r.select(data, data.getTableName(), "((syncparam & " + Long.toString(flag) + ") <> 0)", "created desc"))
			ret = data.created;
		r.close();
		return ret;
	}
}
