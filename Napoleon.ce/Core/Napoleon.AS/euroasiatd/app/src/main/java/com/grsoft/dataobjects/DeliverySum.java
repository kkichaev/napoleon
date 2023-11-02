package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="deliverysum", keyFields="id")
public class DeliverySum extends DataObject {
	public String id = "";
	public Date date;
	/*Просроченная задолжность*/
	@Scale(Consts.SUM_SCALE)
	public int dsum = 0;
	/*Долг за 3 дня*/
	@Scale(Consts.SUM_SCALE)
	public int dsum2 = 0;
}
