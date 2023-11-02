package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="FolderDiscount",keyFields="id")
public class OrgFolderDiscount extends OrgDiscountBase {
	public List<OrgFolderDiscountItem> items;

}
