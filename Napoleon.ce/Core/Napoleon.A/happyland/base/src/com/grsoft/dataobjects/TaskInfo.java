package com.grsoft.dataobjects;

import java.util.Date;
import com.grsoft.database.TableInfo;


@TableInfo(name="taskinfo", keyFields="id,date,idgr", indexes="done")
public class TaskInfo extends DataObject {
	public static final String TASK_GROUP_KEY = "ГруппыЗадач";
	public static final String TASK_GROUP_DELIMITER = ";";
	
	/***
	 * Код контрагента
	 */
	public String id = "";
	
	/***
	 * Текст задачи
	 */
	public String text = "";
	
	/***
	 * Группа
	 */
	public String idgr = "";
	
	/***
	 * 1 - выполнена
	 */
	public int done = 0;
	
	/***
	 * Дата создания без времени
	 */
	public Date date;
	
	/***
	 * Дата выполнения
	 */
	public Date donedate;
	
	/***
	 * Флаги
	 */
	public int params;
	
	/***
	 * Дата создания документа
	 */
	public Date docdate;
}
