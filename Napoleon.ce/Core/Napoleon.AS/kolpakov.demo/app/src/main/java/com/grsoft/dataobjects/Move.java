package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="move", keyFields = "created")
public class Move extends CreateDocDataObject {
	public String src = "";
	public String dst = "";
	public List<MoveItem> items = new ArrayList<MoveItem>();
	public int fsrc = 0;
	public int fdst = 0;
	public int sumType = 0;
}
