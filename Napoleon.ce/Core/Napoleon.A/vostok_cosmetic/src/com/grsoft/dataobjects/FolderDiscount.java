package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="FolderDiscount", keyFields="fid")
public class FolderDiscount extends DataObject {
	@Scale(value=Consts.SUM_SCALE)
	public int discount;
	
	public int folderID;
	
	public String fid;
}
