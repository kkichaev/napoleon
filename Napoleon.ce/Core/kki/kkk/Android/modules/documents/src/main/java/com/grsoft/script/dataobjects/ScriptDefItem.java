package com.grsoft.script.dataobjects;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.FieldOrder;
import com.grsoft.types.FieldVersion;
import com.grsoft.types.Scale;

public class ScriptDefItem extends DataObject {

	/**
	 * »дентификатор текущего документа
	 */
	@FieldOrder(order=0)
	public String curType = "";
	
	/**
	 * Ќомер строки дл€ следующего документа
	 * -1 или число > size() последний документ
	 */
	@FieldOrder(order=1)
	public int nextDoc;
	
	/**
	 * “ип услови€ перехода к следующему документу
	 * 0 - текущего документа может не быть
	 * 1 - документ должен быть заполнен
	 */
	@Scale(value=1)
	@FieldOrder(order=2)
	public int condition;
	
	/**
	 * ѕараметр дл€ услови€, если надо
	 */
	@FieldOrder(order=3)
	public String condParam = "";
	
	/**
	 * Ќазвание пункта сценари€, если нет - название документа
	 */
	@FieldOrder(order=4)
	public String name = "";

	@FieldOrder(order=5)
	@FieldVersion(version=1)
	public int pos = 0;
	
	public boolean canSkip() { return (condition == 0); }

	@FieldOrder(order=6)
	@FieldVersion(version=2)
	public String id = "";
}
