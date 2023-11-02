package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgDogovors extends DataObject {
	@FieldOrder(order=0)
	public String id;

	@FieldOrder(order=1)
	public String name;
	
	@FieldOrder(order=2)
	public String idOrg;

	@FieldOrder(order=3)
	public String idPay;
	
	@FieldOrder(order=4)
	@Scale(value=Consts.SUM_SCALE)
	public int payLimit;

	@FieldOrder(order=5)
	public int checkPay;

	@FieldOrder(order=6)
	public int dayLimit;

	@FieldOrder(order=7)
	public int checkDay;

	@FieldOrder(order=8)
	@Scale(value=Consts.SUM_SCALE)
	public int maxOrder;
	
	@FieldOrder(order=9)
	public int outDays;
	
	@FieldOrder(order=10)
	public String limitMsg;
	
	@FieldOrder(order=11)
	public int isMain;
	
	@Override
	public String toString() {
		return name;
	}
}
