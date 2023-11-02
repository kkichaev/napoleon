package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrgDogovor extends DataObject {
	@FieldOrder(order=0)
	public String id;

	@FieldOrder(order=1)
	public String name;
	
	@FieldOrder(order=2)
	@Scale(value=Consts.SUM_SCALE)
	public int limit;
	
	@FieldOrder(order=3)
	public String type;

	@FieldOrder(order=4)
	@Scale(value = Consts.SUM_SCALE)
	public int deb;
	
	@FieldOrder(order=5)
	@Scale(value = Consts.SUM_SCALE)
	public int outdeb; 
	
	@FieldOrder(order=6)
	@Scale(value = Consts.SUM_SCALE)
	public int minOrder;
	
	@FieldOrder(order=7)
	public int stop;
	
	@Override
	public String toString() {
		String text = name + " " + Util.IntToScaleStr(deb, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		if(stop != 0)
			text = "! " + name;
		return text;
	}
}
