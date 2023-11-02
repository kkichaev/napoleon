package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

/***
 * Печатные формы 
 * @author kki
 *
 */
@TableInfo(name="pform", keyFields="name")
public class PrintForm extends DataObject{
	/***
	 * Наименование
	 */
	public String name = "";
	
	/***
	 * Форма
	 */
	public byte[] form;
}
