package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Report;

public class ReportImpl extends DbObject<Report> {

	public static boolean haveReport(String name) {
		ReportImpl ri = new ReportImpl();
		Report r = ri.getData();
		r.id = name;
		boolean have = ri.read();
		ri.close();
		return have;
	}

}
