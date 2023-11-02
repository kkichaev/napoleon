package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="OrgTask", keyFields="id,created")
public class Task extends DataObject {
	
	/***
	 * Дата выполнени
	 */
	public Date dodate;
	
	/***
	 * Дата создания
	 */
	public Date created;
	
	/***
	 * Id контрагента
	 */
	public String id = "";
	
	/***
	 * Задача
	 */
	public String task = "";
	
	/**
	 * Отчет о выполнении
	 */
	public String done = "";
	
	/***
	 * Пользователь 
	 */
	public String userid = "";
}
