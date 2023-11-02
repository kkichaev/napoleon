package com.grsoft.ads.dataobjects;

import java.util.Date;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="taskquery", keyFields="taskid")
public class TaskQuery extends DataObject {
	public static final int NEW = 0;
	public static final int RESOLVED = 1;
	public static final int REJECT = 2;
	public static final int APPLY = 4;
	public static final int INWORK = 5;
	
	public Date created;
	public String taskid = "";
	public String userid = "";
	public String text = "";
	public String id = "";
	public Date start;
	public Date finish;
	public String fio = "";
	public String phone = "";
	public int solution = 0;
	public Date exectime;
	public String execrem = "";
	public int longitude = 0;
	public int latitude = 0;
	public String execuser = "";
	public int uptoday = 0;
	public String client = "";
	public String address = "";
}
