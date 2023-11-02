package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="actions", keyFields="id")
public class Action extends DataObject {
	public String id;
	public String name;
	public String descr;
	public Date start;
	public Date finish;
	public int level;


	public List<ActionItem> items = new ArrayList<ActionItem>();
	public List<ActionFile> files = new ArrayList<ActionFile>();
}
