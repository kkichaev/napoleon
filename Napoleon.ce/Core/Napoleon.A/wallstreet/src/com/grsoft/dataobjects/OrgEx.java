package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.util.Util;

public class OrgEx extends Org {
	public String info = "";
	public int delay;
	public String category = "";
	public Date license = new Date();
	
	public List<MatrixItem> matrix = new ArrayList<MatrixItem>();
	public List<MatrixItem> reject = new ArrayList<MatrixItem>();
	
	public boolean noLicense(Date d) {
		return (license.compareTo(Util.getDayStart(d)) <= 0);
	}
}
