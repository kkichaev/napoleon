package com.grsoft.napoleon.modules.print.util;
import com.grsoft.aceteam.R;

import com.grsoft.dataobjects.impl.DbObject;


/***
 * 
 * @author kki
 */
public interface MakeDocNumberStartegy {
	/**
	 * Выдает следующий номер. Автоматом не сохраняется т.к. может проихойти отмена создания документа
	 * @param table
	 * @return
	 */
	String makeNextDocNumber(DbObject<?> table);
	
	/**
	 * Сохраняем последний номер
	 * @param table
	 * @param number
	 */
	void saveDocNumber(String table, String number);
}
