package com.grsoft.dataobjects;

public class OrgEx extends Org {
	public String typeID = "";
	public String categID = "";
	public String visitTypeID = "";
	public String freq = "";
	public String nameTP = "";
	public String typeTPID = "";

	@Override
	public boolean isPotencial() {
		return false;
	}
}
