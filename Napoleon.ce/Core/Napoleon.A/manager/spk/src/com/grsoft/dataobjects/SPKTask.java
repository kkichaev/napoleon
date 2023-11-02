package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="spktask", keyFields="created", indexes="agentid,start,finish,params")
public class SPKTask extends CreateDocDataObject {
	public String agentid = "";
	public Date start;
	public Date finish;
	public int status = 0;
	public String skill = "";
	public String strengths = "";
	public String razvitie = "";
	public String task = "";
}
