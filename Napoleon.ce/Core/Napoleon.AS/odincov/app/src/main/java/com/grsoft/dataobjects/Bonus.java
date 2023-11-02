package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="Bonus", keyFields="created", indexes="order,def")
public class Bonus extends Order {
	public Date order;
	public String def;
}
