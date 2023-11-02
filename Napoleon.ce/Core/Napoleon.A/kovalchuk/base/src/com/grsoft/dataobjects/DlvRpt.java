package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="dlvrpt")
@ServerInfo(name="DlvRpt")
public class DlvRpt extends DataObject {
	public String encode;
	public byte[] report;
}
