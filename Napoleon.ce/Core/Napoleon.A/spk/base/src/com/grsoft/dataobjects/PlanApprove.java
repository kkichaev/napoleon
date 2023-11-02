package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="planapprove", keyFields="plan")
public class PlanApprove extends DataObject {
	public Date plan;
}
