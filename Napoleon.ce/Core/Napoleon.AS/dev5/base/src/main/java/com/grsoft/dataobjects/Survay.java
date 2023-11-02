/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Survay 
 * 
 * kki   17/11/2010   creating
 */

package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import java.util.Date;

import com.grsoft.types.Scale;

public class Survay extends DataObject
{
	public String fid = ""; 
	public String choice = "";
	public String id = "";
	public Date date;
	
	@Scale(value=1)
	public int ordflag;
}
