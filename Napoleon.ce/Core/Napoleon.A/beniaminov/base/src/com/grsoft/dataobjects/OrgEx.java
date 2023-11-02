package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrgEx extends OrgPrint {
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
}
