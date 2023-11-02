/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   21/08/2011   creating
 */
package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import java.util.Date;
import com.grsoft.database.TableInfo;

/***
 * Заметка по организацие
 * @author kki
 *
 */

@TableInfo(name="orgnotes", keyFields="id")
public class OrgNotes extends DataObject {
	/***
	 * Связь с таблицей Org поле id (код организации)
	 */
	public String id = "";
	
	/***
	 * Текст заметки
	 */
	public String text = "";
	
	/***
	 * Дата изменения
	 */
	public Date date;
	
}
