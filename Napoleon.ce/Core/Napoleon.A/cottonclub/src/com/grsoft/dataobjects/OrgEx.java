package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org implements Comparable<OrgEx> {
	public String city = "";
	public String retail = "";
	public List<MatrixItem> price = new ArrayList<MatrixItem>();
	
	@Override
	public int compareTo(OrgEx another) {
		return name.compareTo(another.name);
	}
}
