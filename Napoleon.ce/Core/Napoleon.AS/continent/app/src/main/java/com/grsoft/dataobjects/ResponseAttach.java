package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="responseattach", keyFields="created", indexes="doc")
@ServerInfo(name="ResponseAttach")
public class ResponseAttach extends DataObject {
	public Date created;
	public int doc;
	public String id;
}
