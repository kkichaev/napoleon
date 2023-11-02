package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="pricefolder", keyFields="id")
@ServerInfo(name="PriceFolder")
public class PriceFolder extends DataObject {
	public String id = "";
	public List<PriceFolderItem> items = new ArrayList<PriceFolderItem>();
}
