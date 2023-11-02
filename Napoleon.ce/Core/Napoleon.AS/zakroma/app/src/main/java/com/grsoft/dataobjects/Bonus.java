package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="bonus", keyFields="created", indexes="order,action")
public class Bonus extends Order {
	public Date order = new Date();
	public String action = "";
	public int whNumber = 0;
}
