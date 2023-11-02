/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Folder
 *
 * kki   25/11/2010   creating
 */
package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="Folder", keyFields = "id", indexes="fid")
public class Folder extends DataObject
{
	public int id;
	public String name;
	public int level;
	
	public String fid = "";
}
