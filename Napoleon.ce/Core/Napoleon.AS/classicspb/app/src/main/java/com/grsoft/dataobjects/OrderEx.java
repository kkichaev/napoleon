package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order implements OrderBase {
	public final static String NOT_BONUS = "нет";
	
	public String whName = "";	
	public int bonus = 0;
	public String bonusAdd = "";
	
	public String dogovor = "";
	
	@Scale(value=Consts.SUM_SCALE)
	public int sumDlv = 0;
	
	public int sumDlvFlag = 0;
	

	public String getWhName() { return whName; }
	public void setWhName(String name) { whName = name; }

	public int getWhIndex() { return whIndex; }
	public void setWhIndex(int index) { whIndex = index; }
}
