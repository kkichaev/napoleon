package com.grsoft.dataobjects;

public class OrgEx extends Org implements OrgBase {
	public String matrix;
	public String faceMatrix = "";
	public String orgType;
	
	public String ido = "";

	public String getMatrix() { return matrix; }
	public String getOrgType() { return orgType; }
	@Override public String getIDO() { return ido; }
}
