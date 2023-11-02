package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;

@ServerInfo(name="PriceFolderOrder")
public class PriceFolderOrder extends DataObject {
	public String id = "";
	public int folderID = 0;
	public int ord;
}
