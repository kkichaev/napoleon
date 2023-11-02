package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order {

	static public final int ofSert      = 0x0004;
	static public final int ofQuality   = 0x0008;
	static public final int ofDate      = 0x0010;
	static public final int ofPayBefore = 0x0020; // без оплаты не отгружать
	static public final int ofFact      = 0x0040; //отсрочка - нал/факт

	@Scale(value=Consts.SUM_SCALE)
	public int collectSum; // инкассация
	public String collectNum = ""; // номер ТТН

	public String logistic = ""; // примечание логиста
	public String fcontrol = ""; // примечание финконтроль
	
	public Date collectDate;

	public List<DiscountAction> discact = new ArrayList<DiscountAction>();
}
