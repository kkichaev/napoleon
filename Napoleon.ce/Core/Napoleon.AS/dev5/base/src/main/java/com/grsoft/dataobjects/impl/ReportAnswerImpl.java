package com.grsoft.dataobjects.impl;
import com.grsoft.aceteam.R;

import com.grsoft.dataobjects.ReportAnswer;

public class ReportAnswerImpl extends DbObject<ReportAnswer> {
	public static boolean haveData(String id) {
		ReportAnswerImpl ri = new ReportAnswerImpl();
		ReportAnswer r = ri.getData();
		r.id = id;
		boolean res = ri.read();
		ri.close();
		return res;
	}
}
