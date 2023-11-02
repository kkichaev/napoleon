package com.grsoft.dataobjects;

import java.util.Date;

public class OrderEx extends Order {
	public String payType;
	public String dogovor;
	public Date dlvDate;
	
	public int bill = 0;
	public int fastDlv = 0;
}
