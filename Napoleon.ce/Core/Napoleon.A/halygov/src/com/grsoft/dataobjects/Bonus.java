package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="bonus", keyFields="created", indexes="id,order")
public class Bonus extends Order {
	public Date order;
	
	@Scale(value=Consts.SUM_SCALE)
	public int ordersum;
}
