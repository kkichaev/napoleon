package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class DlvDebet extends DataObject {
	public String number = "";
	
	@Scale(value = Consts.SUM_SCALE)
	public int sum = 0;
	
	public String id = "";
}
