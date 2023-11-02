package com.grsoft.dataobjects;

import java.util.Date;

public class OrderEx extends Order {
	public Date from = new Date();
	public Date till = new Date();
	public int loadedFromKIS = 0;

	public String docNumber = "";

	public int dtp = 0;
}

