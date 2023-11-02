package com.grsoft.dataobjects;

import java.util.Date;
import com.grsoft.database.TableInfo;

@TableInfo(name="dlvhighlight", keyFields="created")
public class DlvHighlight extends DataObject {
	public Date created;
}
