/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Config
 *
 * kki   19/11/2010   creating
 */
package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.database.TableInfo;

@TableInfo(name="Config", keyFields = "key")
public class Config extends DataObject
{
	public String key = "";
	public String value = "";
}
