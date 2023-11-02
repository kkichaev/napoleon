package com.grsoft.ads.dataobjects;

import java.util.Date;

import com.grsoft.types.Scale;

public class UserOrderEx extends UserOrder{
	/***
	 * Тип работ
	 */
	public String worktype = "";
	
	/***
	 * Фамилия клиента
	 */
	public String contact = "";
	
	/***
	 * Тип счетчика
	 */
	public String counter = "";
	
	/***
	 * Номер счетчика
	 */
	public String numctr = "";
	
	/***
	 * Номер протокола
	 */
	public String protocol = "";
	
	/***
	 * Номер свидельства
	 */
	public String certificate = "";
	
	/***
	 * Показания счетчика
	 */
	@Scale(value = 1000)
	public int datactr = 0;
	
	/***
	 * Телефон клиента
	 */
	public String phone = "";
	
	/***
	 * Дата начала работ
	 */
	public Date begin;
	
	/***
	 * Дата окончания работ
	 */
	public Date end;
}
