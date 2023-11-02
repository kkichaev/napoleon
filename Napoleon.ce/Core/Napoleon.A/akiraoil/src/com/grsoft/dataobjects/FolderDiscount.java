package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="FolderDiscount", keyFields="agreeId")
@ServerInfo(name="FolderDiscount")
public class FolderDiscount extends DataObject {
	public String agreeId = "";
	public List<FolderDiscountItem> items = new ArrayList<FolderDiscountItem>();

}
