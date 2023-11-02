package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.CreateDocDataObject;

@TableInfo(name="responce", keyFields="id,date")
public class Responce extends CreateDocDataObject {
	public List<VisitItem> items = new ArrayList<VisitItem>();
}
