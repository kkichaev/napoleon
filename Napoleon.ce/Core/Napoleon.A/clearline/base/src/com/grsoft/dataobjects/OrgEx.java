package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrgEx extends OrgPrint {
	public static final int TAX_INCLUDE = 0;
	public static final int TAX_ABOVE = 1;
	public static final int TAX_NONE = 2;

	public static final int VETIS_NONE = 0;
	public static final int VETIS_CONFIRM = 1;
	public static final int VETIS_NO = 2;
	public static final int VETIS_UNCONFIRM = 3;
	public static final int VETIS_EXCLUDE = 4;
	
	public String stopMsg = "";
	
	public String ido = "";
	
	public String dogovor = "";
	public Date dogDate = new Date();
	public String dogNum = "";
	
	public String divName = "";

	public String payName = "";
	public String payPhone = "";
	public String payBank = "";
	public String payInn = "";
	
	public String orgCreateInn = "";
	
	public List<OrgCost> orgCost = new ArrayList<OrgCost>();
	
	@Override
	public boolean isStopList() {
		return stopMsg.length() > 0; 
	}
	
	public int kpk = 0;
	
	/*
	 * 0 - безнал
	 * 1 - нал
	 * */
	public int isBlack = 0;
	
	public int taxType = 0;

	public int isAZS = 0;
	
	public String info = "";
	
	public int vetis = 0;
	
	public boolean canSale() { return (vetis == VETIS_NONE || vetis == VETIS_CONFIRM); }
	
	public String vetisText() {
		return (vetis == VETIS_CONFIRM) ? "Подтвержден" :
			(vetis == VETIS_EXCLUDE) ? "Исключен" :
			(vetis == VETIS_NO) ? "Нет статус" :
			(vetis == VETIS_UNCONFIRM) ? "Не подтвержден" :
			"";
	}
}
