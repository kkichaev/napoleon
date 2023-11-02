package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="actioinTypes", keyFields="id")
public class ActionType extends DataObject {
	public String id;
	public String name;
	
	public List<QuestionItem> items = new ArrayList<QuestionItem>();
}
