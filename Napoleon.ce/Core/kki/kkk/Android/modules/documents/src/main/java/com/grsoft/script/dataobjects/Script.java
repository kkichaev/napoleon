package com.grsoft.script.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="Scripts", keyFields="created")
public class Script extends CreateDocDataObject {
	
	@Scale(value=Consts.SUM_SCALE)
	public long sum;
	
	public int scriptId;
	
	public List<ScriptItem> items = new ArrayList<ScriptItem>();
}
