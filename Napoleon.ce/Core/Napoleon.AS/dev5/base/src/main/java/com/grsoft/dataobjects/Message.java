/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Объект Message,
 * содержит сообщение для пользователя
 *
 * kki   06/04/2011   creating
 */
package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="message", keyFields = "date")
public class Message extends DataObject {
	public Date date;
	public String message = "";
}
