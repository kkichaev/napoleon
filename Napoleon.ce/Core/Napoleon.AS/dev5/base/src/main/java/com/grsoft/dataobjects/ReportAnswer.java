package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="reportAnswer", keyFields="id")
public class ReportAnswer extends DataObject {
	public Date rcvdDate = new Date();
	public String id = "";
	public String encoding = ""; 
	public byte[] report;
}
