package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="FolderDiscount", keyFields="folderID")
@ServerInfo(name="FolderDiscount")
public class FolderDiscount extends DataObject {
	public int folderID = 0;
	
	@Scale(value = Consts.SUM_SCALE)
	public int discount = 0;
}
