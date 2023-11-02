package com.grsoft.dataobjects;


public class OrderEx extends Order {
	public static final int PICKUP_FLAG = 1;
	public static final int PASPORT_FLAG = 2;
	public static final int SERT_FLAG = 4;
	public static final int SHEMA_PROEZDA_FLAG = 8;
	public static final int PREDSTAVIT_FLAG = 16;
	public static final int BILL_FLAG = 32;
	
	public String regCode = "";
	public String whCode = "";
	public int whIndex;
	
	/**
	 * грузополучатель
	 */
	public String consignee = "";
	
	/**
	 * Грузоприемщик
	 */
	public String gprm = "";
	
	/**
	 * Плательщик
	 */
	public String payer = "";
	
	public int paramsex;
	
	public String dogCode = "";
	
	public String executiveManager = "";
}
