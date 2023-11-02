package com.grsoft.dataobjects;


public class ReturnEx extends Return implements OrgUnitable {
	public String unitCode = "";
	public String costType = "";
	
	@Override public String getCode() { return unitCode; }
	@Override public void setCode(String val) { unitCode = val; }
}
