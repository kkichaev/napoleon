package com.grsoft.dataobjects;

import java.util.Date;
import com.grsoft.database.TableInfo;

@TableInfo(name="offer", keyFields="created")
public class Offer extends OrderPrint {
	public Date start;
	public Date finish;
	public String paytime;
	public String agreement = "";
}
