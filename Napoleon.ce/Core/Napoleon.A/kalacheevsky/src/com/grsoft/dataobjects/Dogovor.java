package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="dogovor", keyFields="id,num")
public class Dogovor extends DataObject {
	
	/***
	 * Основной договор
	 */
	private static final int GENERAL = 0x2;
	
	/**
	 * блокировка по организации
	 */
	private static final int BLOCKED = 0x4;

	/***
	 * ID Контрагента
	 */
	public String id;
	
	/***
	 * Номер договора
	 */
	public String num;
	
	/***
	 * Наименование
	 */
	public String name;
	
	/***
	 * Флаги
	 */
	public int flags;
	
	/***
	 * Тип цены
	 */
	public int ctype;
	
	/***
	 * Основной договор
	 */
	public boolean isGeneral(){
		return (flags & GENERAL) == GENERAL;
	}
	
	public boolean isBlocked() { return ((flags & BLOCKED) == BLOCKED); } 
	
	/***
	 * Код организации
	 */
	public String firm;
}
