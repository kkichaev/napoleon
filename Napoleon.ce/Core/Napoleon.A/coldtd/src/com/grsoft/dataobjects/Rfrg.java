package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="Rfrg", keyFields="id", indexes="ido:rfid")
public class Rfrg extends DataObject {
	/**
	 * Номер холодильника
	 */
	public String id = "";
	
	/**
	 * Код контрагента
	 */
	public String ido = "";
	
	/**
	 * Инвентарный номер
	 */
	public String inv = "";
	
	public String type = "";
	public String model = "";
	public String rfid = "";
}
