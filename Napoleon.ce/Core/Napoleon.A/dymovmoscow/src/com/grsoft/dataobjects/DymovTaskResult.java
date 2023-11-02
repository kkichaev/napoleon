package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="DymovTaskResult", keyFields="idTask", indexes="id,done,created")
public class DymovTaskResult extends DataObject {
	
	public static final int EXPORTED = 1; 
	
	public Date created;
	public Date done;
	
	public String idTask = "";
	public String id = "";
	public String task = "";
	public String remark = "";
	
	public int flags; 
}
