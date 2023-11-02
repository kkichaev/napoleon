package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="spancop", keyFields="created", indexes="id")
public class Spancop extends CreateDocDataObject {
	public int realclient;
	public String category;
	public String segment;
	public Date first;
	public String success;
	public String chance;
	@Scale(value=Consts.WEIGHT_SCALE)
	public int cub;
	public Date s;
	public Date p1;
	public Date a;
	public Date n;
	public Date c;
	public Date o;
	public Date p2;
	public String holding;
	public String clientLevel;
	public String competitor;
}
