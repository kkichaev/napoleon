package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.database.TableInfo;

@TableInfo(name="attachment", keyFields="id")
public class Attachment extends DataObject {
	public String id = "";
	public String name = "";
	public String path = "";
}
