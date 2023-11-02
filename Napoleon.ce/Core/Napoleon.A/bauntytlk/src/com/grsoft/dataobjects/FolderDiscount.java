package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="fdsc", keyFields="folderID,category")
@ServerInfo(name="FolderDiscount")
public class FolderDiscount extends DataObject {
	public int folderID = 0;
	
	@Scale(value = Consts.SUM_SCALE)
	public int discount = 0;

	public String category = "";
}
