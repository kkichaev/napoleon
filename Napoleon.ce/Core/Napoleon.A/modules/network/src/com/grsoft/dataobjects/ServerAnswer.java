/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 *
 *
 * kki   14/10/2010   creating
 */
package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;

@TableInfo(name = "")
public class ServerAnswer extends DataObject
{
	@Scale(value=1)
	public int response;
	public String message;
}
