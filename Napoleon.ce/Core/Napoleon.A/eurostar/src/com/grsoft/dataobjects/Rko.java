package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="rko", keyFields="created")
public class Rko extends Pko {
	public String cause = "";
	public String cash = "";
}
