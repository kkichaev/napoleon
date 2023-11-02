package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	public int delay;
	public String wrkTime;
	
	@Scale(value=Consts.SUM_SCALE)
	public int balance;
	
	public String paytype = "";
	/***
	 * Доступные формы оплаты
	 */
	public String ptypes = "";
}
