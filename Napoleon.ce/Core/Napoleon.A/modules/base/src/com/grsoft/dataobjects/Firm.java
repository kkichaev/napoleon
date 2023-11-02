package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="firm", keyFields="id")
@ServerInfo(name="Firm")
public class Firm extends DataObject {
	
	/***
	 * Код
	 */
	public String id = "";
	
	/***
	 * Наименование
	 */
	public String name = "";
	
	/***
	 * адрес
	 */
	public String address = "";
	
	public String factAddress = "";
	
	/***
	 * Телефон
	 */
	public String phone = "";
	
	/***
	 * Инн
	 */
	public String inn = "";
	
	/***
	 * Счет
	 */
	public String bank = "";
	
	/***
	 * Бухгалтер
	 */
	public String buh = "";
	
	/***
	 * Директор
	 */
	public String chief = "";
	
	/***
	 * оллжность директора
	 */
	public String chiefRange = "";
	
	/***
	 * Полное наименование
	 */
	public String fullName = "";
	
	/***
	 * ОКПО
	 */
	public String okpo = "";

	/***
	 * Отпуск разрешил
	 */
	public String shipApprove = "";
	
	/***
	 * Отгрузил
	 */
	public String shipment = "";
	
	/**
	 * свидетельство ИП
	 */
	public String certificate = "";

	@Override
	public String toString() {
		return name;
	}
}
