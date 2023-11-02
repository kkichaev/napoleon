package com.grsoft.dataobjects;

import java.util.Date;

public class OrgEx extends Org {
	public static final int FL_CAN_ACTION = 8;
	
	public Date license;

	/**
	 * передаем без масштаба - числа бывают в миллионах
	 */
	public int limit;
	
	public String cfo = "";
	
	public String comment = "";

	public String dlvFrom = "";
	public String dlvTill = "";
	
	public String email = "";
	public String kpp = "";
}
