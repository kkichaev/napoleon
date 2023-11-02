package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	public String ido;
	
	public String defDog;
	
	@Scale(value=Consts.SUM_SCALE)
	public int balance;
}
