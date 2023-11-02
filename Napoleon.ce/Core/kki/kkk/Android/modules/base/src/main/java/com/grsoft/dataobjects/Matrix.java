/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   24/06/2011   creating
 */
package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="matrix", keyFields = "name")
public class Matrix extends DataObject{
	
	/***
	 * Имя
	 */
	public String name = "";
	
	/**
	 * Содержание
	 */
	public List<MatrixItem> items = new ArrayList<MatrixItem>();
}
