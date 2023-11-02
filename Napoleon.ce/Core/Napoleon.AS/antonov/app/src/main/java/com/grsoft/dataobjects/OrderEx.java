package com.grsoft.dataobjects;

public class OrderEx extends Order {
	public int payType;
	public String dogovor;
	public String numgroup;
	public int invoiceType;
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
