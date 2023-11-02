package com.grsoft.ads.dataobjects;

import com.grsoft.types.Scale;

public class OrderEx extends Order{
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
}
