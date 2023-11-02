package com.grsoft.dataobjects;


public class Order2Ex extends OrderEx{
	public int wparam;
	
	/***
	 * Значение поля wparam - расходная накладная
	 */
	public static final int NAKL = 1;
	
	/***
	 * Значение поля wparam - счет фактура
	 */
	public static final int CH_FACT = 2;
}
