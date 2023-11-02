package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="DlvMove", keyFields="id,num")
public class DlvMove extends DataObject {
	public String id;
	public String num;
	
	public List<DlvMoveItem> items = new ArrayList<DlvMoveItem>();
}
