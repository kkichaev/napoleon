package com.grsoft.napoleon.documents;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import com.grsoft.napoleon.modules.print.DebtDocList;

public class DebtDocListEx extends DebtDocList {

	public DebtDocListEx(String where, String order, boolean loadDelivery) {
		super(where, order, loadDelivery);
	}

	@Override
	protected void loadSales(String where, Date lastDlvDate, HashSet<String> usedNumbers) {
		if(lastDlvDate != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(lastDlvDate);
			c.add(Calendar.DAY_OF_MONTH, 1);
			lastDlvDate = c.getTime();
		}
		super.loadSales(where, lastDlvDate, usedNumbers);
	}
	
	@Override
	protected void loadPKO(String where, Date lastPayDate, HashSet<String> usedNumbers) {
		if(lastPayDate != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(lastPayDate);
			c.add(Calendar.DAY_OF_MONTH, 1);
			lastPayDate = c.getTime();
		}
		super.loadPKO(where, lastPayDate, usedNumbers);
	}
}
