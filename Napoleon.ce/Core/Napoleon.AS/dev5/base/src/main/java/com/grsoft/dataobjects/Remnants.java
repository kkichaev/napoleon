/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Remnants(Остатки)
 *
 * kki   11/04/2011   creating
 */
package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;

@TableInfo(name="remnants", keyFields = "created")
@ServerInfo(name = "OrgStock")
public class Remnants extends CreateDocDataObject {
	
	/**
	 * Служебный флаг описывающий
	 * текущее состояния 
	 * 
	 * устаревшее - теперь используется params
	 */
	@Scale(value=1)
	@Deprecated
	public int flags;
	
	/**
	 * Содержание
	 */
	public List<RemnantItem> items = new ArrayList<RemnantItem>();
}
