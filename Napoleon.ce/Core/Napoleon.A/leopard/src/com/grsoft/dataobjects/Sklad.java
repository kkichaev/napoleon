package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;

@TableInfo(name="sklad", keyFields="id")
public class Sklad extends DataObject {
	public static int CHECK_PACKS = 1;
	public String id = "";
	public String name = "";
	@Scale(value=1)
	public int flags;
}
